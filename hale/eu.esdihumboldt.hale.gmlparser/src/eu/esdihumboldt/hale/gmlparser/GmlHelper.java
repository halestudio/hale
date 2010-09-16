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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.ApplicationSchemaConfiguration;
import org.geotools.gml3.GMLConfiguration;
import org.geotools.xml.Configuration;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GmlHelper {
	
	/**
	 * Configuration types
	 */
	public enum ConfigurationType {
		/** GML 2.x */
		GML2,
		/** GML 3.x */
		GML3,
		/** GML 3.2 */
		GML3_2,
		/** Application Schema */
		APPLICATION_SCHEMA
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
		return loadGml(xml, type, null, null, null);
	}

	/**
	 * Load GML from an input stream
	 * 
	 * @param xml the XML input stream
	 * @param type the configuration type, defaults to GML3
	 * @param namespace schema namespace (when using {@link ConfigurationType#APPLICATION_SCHEMA})
	 * @param schemaLocation schema location (when using {@link ConfigurationType#APPLICATION_SCHEMA})
	 * @param elements 
	 * 
	 * @return the loaded feature collection or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public static FeatureCollection<FeatureType, Feature> loadGml(InputStream xml, 
			ConfigurationType type, String namespace, String schemaLocation, Iterable<SchemaElement> elements) {
		try {
			Configuration conf;
			switch (type) {
			case GML2:
				conf = new org.geotools.gml2.GMLConfiguration();
				break;
			case GML3_2:
				conf = new org.geotools.gml3.v3_2.GMLConfiguration();
				break;
			case APPLICATION_SCHEMA:
				if (namespace == null || schemaLocation == null) {
					throw new IllegalStateException("Schema namespace and " +
							"location must be specified when using Application " +
							"Schema parsing configuration");
				}
				conf = new HaleSchemaConfiguration(namespace, schemaLocation, elements); //new ApplicationSchemaConfiguration(namespace, schemaLocation);
				break;
			case GML3: // fall through
			default: // default to GML3
				conf = new GMLConfiguration();
			}
			HaleGMLParser parser = new HaleGMLParser(conf);
			Object result = parser.parse(xml);
			
			if (result instanceof FeatureCollection<?, ?>) {
				return (FeatureCollection<FeatureType, Feature>) result;
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

}
