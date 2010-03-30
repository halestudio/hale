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

import org.apache.ws.commons.schema.XmlSchemaElement;
import org.geotools.feature.AttributeTypeBuilder;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Represents the definition of an attribute
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class SchemaAttribute extends AbstractElementAttribute {
	
	/**
	 * Constructor
	 * 
	 * @param declaringType the declaring type, if it is <code>null</code>,
	 *   the attribute type will not be determined
	 * @param name the attribute name
	 * @param typeName the name of the attribute type
	 * @param element the element defining the attribute
	 * @param schemaTypes the schema types
	 */
	public SchemaAttribute(TypeDefinition declaringType, String name, Name typeName,
			XmlSchemaElement element, SchemaTypeResolver schemaTypes) {
		super(declaringType, name, typeName, element);
		
		if (declaringType != null) {
			// set the declaring type
			declaringType.addDeclaredAttribute(this);
			
			// determine the attribute type
			determineAttributeType(element, schemaTypes);
		}
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param other the schema attribute to copy
	 */
	protected SchemaAttribute(SchemaAttribute other) {
		super(other);
	}
	
	/**
	 * Tries to determine the attribute type
	 * 
	 * @param element the schema element 
	 * @param schemaTypes the schema types 
	 */
	protected void determineAttributeType(XmlSchemaElement element, SchemaTypeResolver schemaTypes) {
		TypeDefinition typeDef = TypeUtil.resolveElementType(element, getTypeName(), schemaTypes);
		
		typeDef = checkAttributeType(typeDef);
		
		setAttributeType(typeDef);
	}
	
	/**
	 * Check if the given type definition should be set as the attribute type
	 * 
	 * @param typeDef the type definition
	 * 
	 * @return the type definition that shall be set as the attribute type
	 */
	protected TypeDefinition checkAttributeType(TypeDefinition typeDef) {
		// inspire geometry attributes
		if (getName().equals("geometry") && typeDef != null && 
				!Geometry.class.isAssignableFrom(typeDef.getType().getBinding())) {
			// create an attribute type with a geometry binding
			AttributeTypeBuilder builder = new AttributeTypeBuilder();
			builder.setBinding(Geometry.class);
			builder.setName(getTypeName().getLocalPart());
			builder.setNamespaceURI(getTypeName().getNamespaceURI());
			builder.setNillable(true);
			AttributeType attributeType = builder.buildType();
			
			TypeDefinition result = new TypeDefinition(getTypeName(), attributeType, typeDef.getSuperType());
			result.setDescription(typeDef.getDescription());
			result.setLocation(typeDef.getLocation());
			return result;
		}
		
		// default: leave type untouched
		return typeDef;
	}
	
	/**
	 * @see AttributeDefinition#copyAttribute(TypeDefinition)
	 */
	@Override
	public AttributeDefinition copyAttribute(TypeDefinition parentType) {
		SchemaAttribute copy = new SchemaAttribute(this);
		copy.setParentType(parentType);
		return copy;
	}
	
}
