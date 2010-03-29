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

package eu.esdihumboldt.hale.schemaprovider.provider.internal.apache;

import java.util.Map;

import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefaultResolveAttribute extends DefaultAttribute {
	
	/**
	 * Create a default attribute
	 * 
	 * @param declaringType the declaring type
	 * @param typeName the attribute type name
	 * @param attribute the attribute 
	 * @param types 
	 * @param importedTypes 
	 */
	public DefaultResolveAttribute(TypeDefinition declaringType, Name typeName,
			XmlSchemaAttribute attribute, Map<Name, TypeDefinition> types,
			Map<Name, TypeDefinition> importedTypes) {
		super(declaringType, typeName, attribute, null);
		
		if (declaringType != null) {
			// determine the attribute type
			determineAttributeType(types, importedTypes);
		}
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param other
	 */
	protected DefaultResolveAttribute(DefaultResolveAttribute other) {
		super(other);
	}
	
	/**
	 * Tries to determine the attribute type
	 * 
	 * @param featureTypes the local feature types
	 * @param importedFeatureTypes the imported feature types
	 */
	protected void determineAttributeType(Map<Name, TypeDefinition> featureTypes, Map<Name, TypeDefinition> importedFeatureTypes) {
		TypeDefinition typeDef = TypeUtil.resolveAttributeType(getTypeName(), featureTypes, importedFeatureTypes);
		
		setAttributeType(typeDef);
	}

	/**
	 * @see AttributeDefinition#copyAttribute(TypeDefinition)
	 */
	@Override
	public AttributeDefinition copyAttribute(TypeDefinition parentType) {
		DefaultResolveAttribute copy = new DefaultResolveAttribute(this);
		copy.setParentType(parentType);
		return copy;
	}
	
}
