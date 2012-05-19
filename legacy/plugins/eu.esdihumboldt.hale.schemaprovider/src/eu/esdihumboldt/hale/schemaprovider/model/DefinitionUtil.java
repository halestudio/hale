/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.schemaprovider.model;

import org.opengis.feature.type.FeatureType;

/**
 * Utility methods related to {@link Definition}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@Deprecated
public abstract class DefinitionUtil {
	
	/**
	 * Get the type definition from a definition. For 
	 * {@link AttributeDefinition}s the attribute type will be returned.
	 * 
	 * @param def the definition
	 * @return the type definition
	 */
	public static TypeDefinition getType(Definition def) {
		if (def instanceof SchemaElement) {
			return ((SchemaElement) def).getType();
		}
		else if (def instanceof TypeDefinition) {
			return (TypeDefinition) def;
		}
		else {
			return ((AttributeDefinition) def).getAttributeType();
		}
	}
	
	/**
	 * Get the feature type from a definition. For 
	 * {@link AttributeDefinition}s the attribute type will be returned.
	 * 
	 * @param def the definition
	 * @return the feature type, may be <code>null</code>
	 */
	public static FeatureType getFeatureType(Definition def) {
		if (def instanceof SchemaElement) {
			return ((SchemaElement) def).getFeatureType();
		}
		else if (def instanceof TypeDefinition) {
			return ((TypeDefinition) def).getFeatureType();
		}
		else {
			return ((AttributeDefinition) def).getAttributeType().getFeatureType();
		}
	}

}
