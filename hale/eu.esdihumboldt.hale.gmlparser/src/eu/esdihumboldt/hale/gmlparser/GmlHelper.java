/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlparser;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.xml.Configuration;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.gmlparser.gml3.HaleGMLConfiguration;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GmlHelper {
	
	private static final Log log = LogFactory.getLog(GmlHelper.class);
	
	/**
	 * Configuration types
	 */
	public enum ConfigurationType {
		/** GML 2.x */
		GML2,
		/** GML 3.x */
		GML3,
		/** GML 3.2 */
		GML3_2
	}
	
	/**
	 * Load GML from an input stream
	 * 
	 * @param xml the XML input stream
	 * @param type the configuration type, defaults to GML3
	 * 
	 * @return the loaded feature collection or <code>null</code>
	 */
	public static FeatureCollection<FeatureType, Feature> loadGml(InputStream xml, 
			ConfigurationType type) {
		return loadGml(xml, type, false, null, null, null);
	}
	
	/**
	 * Load GML from an input stream and use the application schema for parsing
	 * 
	 * @param xml the XML input stream
	 * @param type the configuration type, defaults to GML3
	 * @param namespace schema namespace
	 * @param schemaLocation schema location
	 * @param elements the schema elements
	 * 
	 * @return the loaded feature collection or <code>null</code>
	 */
	public static FeatureCollection<FeatureType, Feature> loadGml(InputStream xml, 
			ConfigurationType type, String namespace, 
			String schemaLocation, Iterable<SchemaElement> elements) {
		return loadGml(xml, type, true, namespace, schemaLocation, elements);
	}
	
	/**
	 * Load GML from an input stream
	 * 
	 * @param xml the XML input stream
	 * @param type the configuration type, defaults to GML3
	 * @param schema schema
	 * 
	 * @return the loaded feature collection or <code>null</code>
	 */
	public static FeatureCollection<FeatureType, Feature> loadGml(
			InputStream xml, ConfigurationType type, Schema schema) {
		return loadGml(xml, type, true, schema.getNamespace(),
			schema.getLocation().toString(), schema.getElements().values());
	}

	/**
	 * Load GML from an input stream
	 * 
	 * @param xml the XML input stream
	 * @param type the configuration type, defaults to GML3
	 * @param useAppSchema if the application schema shall be used for parsing
	 * @param namespace schema namespace
	 * @param schemaLocation schema location
	 * @param elements the schema elements
	 * 
	 * @return the loaded feature collection or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static FeatureCollection<FeatureType, Feature> loadGml(InputStream xml, 
			ConfigurationType type, boolean useAppSchema, String namespace, 
			String schemaLocation, Iterable<SchemaElement> elements) {
		try {
			final Configuration conf;
			if (useAppSchema) {
				if (namespace == null || schemaLocation == null) {
					throw new IllegalStateException("Schema namespace and " +
							"location must be specified when using Application " +
							"Schema parsing configuration");
				}
				
				conf = new HaleSchemaConfiguration(
						type, namespace, schemaLocation, elements);
			}
			else {
				switch (type) {
				case GML2:
					conf = new org.geotools.gml2.GMLConfiguration();
					break;
				case GML3_2:
					conf = new eu.esdihumboldt.hale.gmlparser.gml3_2.HaleGMLConfiguration();
					break;
				case GML3: // fall through
				default: // default to GML3
					conf = new HaleGMLConfiguration(); //new GMLConfiguration();
				}
			}
			HaleGMLParser parser = new HaleGMLParser(conf);
			Object result = parser.parse(xml);

			if (result instanceof Feature && ((Feature) result).getProperty("featureMember") != null) {
				result = ((Feature) result).getProperty("featureMember").getValue();
			}
			
			if (result instanceof FeatureCollection<?, ?>) {
				return (FeatureCollection<FeatureType, Feature>) result;
			}
			else if (result instanceof SimpleFeature[]) {
				SimpleFeature[] features = (SimpleFeature[]) result;
				CstFeatureCollection fc = new CstFeatureCollection();
				for (int i = 0; i < features.length; i++) {
					fc.add(features[i]);
				}
				return fc;
			}
			else if (result instanceof Feature) {
				CstFeatureCollection fc = new CstFeatureCollection();
				fc.add((Feature) result);
				return fc;
			}
			else if (result instanceof Map<?, ?>) {
				// extract features from Map
				Object featureMember = ((Map<?, ?>)result).get("featureMember");
				CstFeatureCollection fc = new CstFeatureCollection();
				if (featureMember instanceof Feature) {
					fc.add((Feature) featureMember);
				}
				else {
					// assume collection
					Collection<? extends Feature> features = (Collection<? extends Feature>) featureMember;
					fc.addAll(features);
				}
				return fc;
			}
			
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	/**
	 * Load GML from an input stream and use the application schema for parsing
	 * if possible
	 * 
	 * @param xml the XML input stream
	 * @param type the configuration type, defaults to GML3
	 * @param schemaLocation schema location, if it is <code>null</code>
	 *   the application schema won't be used for parsing
	 * 
	 * @return the loaded feature collection or <code>null</code>
	 */
	@SuppressWarnings("null")
	public static FeatureCollection<FeatureType, Feature> loadGml(InputStream xml, 
			ConfigurationType type, URI schemaLocation) {
		ApacheSchemaProvider asp = new ApacheSchemaProvider();
		
		Schema schema;
		List<SchemaElement> elements = new ArrayList<SchemaElement>();
		if (schemaLocation != null) {
			try {
				schema = asp.loadSchema(schemaLocation, null);
				elements.addAll(schema.getElements().values());
			} catch (Exception e) {
				schema = null;
				log.warn("Could not load source schema");
			}
		}
		else {
			schema = null;
		}
		
		if (schema == null || elements.isEmpty()) {
			// don't use app schema
			return loadGml(xml, type);
		}
		else {
			// use app schema
			return loadGml(xml, type, true, schema.getNamespace(), 
					schemaLocation.toString(), elements);
		}
	}

	/**
	 * Try to determine the GML version used from the data
	 * 
	 * @param xml the input stream of the source file
	 * @param def the default version to use as fallback, may not be <code>null</code>
	 * 
	 * @return the GML version that was determined or the given default if
	 *    the detection fails 
	 */
	@SuppressWarnings("unchecked")
	public static ConfigurationType determineVersion(InputStream xml,
			ConfigurationType def) {
		ConfigurationType result = null;
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try {
			String gmlNamespace = null;
			
			XMLEventReader reader = inputFactory.createXMLEventReader(xml);
			while (result == null && reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				
				if (gmlNamespace == null) {
					// try to determine gml namespace
					if (!event.isStartDocument() && event.isStartElement()) {
						StartElement element = event.asStartElement();
						Iterator<Namespace> itNs = element.getNamespaces();
						while (gmlNamespace == null && itNs.hasNext()) {
							Namespace ns = itNs.next();
							if (ns.getNamespaceURI().startsWith("http://www.opengis.net/gml")) {
								gmlNamespace = ns.getNamespaceURI();
								
								// detect GML 3.2 from namespace
								if (gmlNamespace.equals("http://www.opengis.net/gml/3.2")) {
									result = ConfigurationType.GML3_2;
								}
							}
						}
					}
				}
				else {
					// check for elements specific to a GML version
					if (event.isStartElement()) {
						StartElement element = event.asStartElement();
						QName name = element.getName();
						if (name.getNamespaceURI().equals(gmlNamespace)) {
							// Geotools GML3 configuration doesn't support gml:coordinates -> GML2
							if (name.getLocalPart().equals("coordinates")) {
								result = ConfigurationType.GML2;
							}
							
							// Geotools GML2 configuration doesn't support gml:posList -> GML3
							if (name.getLocalPart().equals("posList")) {
								result = ConfigurationType.GML3;
							}
						}
					}
				}
			}
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (result != null) {
			return result;
		}
		else {
			return def;
		}
	}

}
