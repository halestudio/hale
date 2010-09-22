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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

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
					conf = new GMLConfiguration();
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
	
}
