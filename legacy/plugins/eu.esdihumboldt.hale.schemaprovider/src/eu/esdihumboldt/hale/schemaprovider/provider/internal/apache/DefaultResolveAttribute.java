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

import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaUse;
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
@Deprecated
public class DefaultResolveAttribute extends DefaultAttribute {
	
	/**
	 * Create a default attribute
	 * 
	 * @param declaringType the declaring type
	 * @param typeName the attribute type name
	 * @param attribute the attribute 
	 * @param schemaTypes the schema types 
	 * @param use the attribute use
	 */
	public DefaultResolveAttribute(TypeDefinition declaringType, Name typeName,
			XmlSchemaAttribute attribute, SchemaTypeResolver schemaTypes, XmlSchemaUse use) {
		super(declaringType, typeName, attribute, null, use);
		
		if (declaringType != null) {
			// determine the attribute type
			determineAttributeType(schemaTypes);
		}
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param other the attribute top copy
	 */
	protected DefaultResolveAttribute(DefaultResolveAttribute other) {
		super(other);
	}
	
	/**
	 * Tries to determine the attribute type
	 * 
	 * @param schemaTypes the schema types 
	 */
	protected void determineAttributeType(SchemaTypeResolver schemaTypes) {
		TypeDefinition typeDef = TypeUtil.resolveAttributeType(getTypeName(), schemaTypes);
		
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
