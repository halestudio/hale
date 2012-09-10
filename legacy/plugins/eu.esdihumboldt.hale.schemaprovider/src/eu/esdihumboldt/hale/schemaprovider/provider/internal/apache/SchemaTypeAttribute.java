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

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Represents the definition of an attribute
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public class SchemaTypeAttribute extends AbstractElementAttribute {

	/**
	 * Constructor
	 * 
	 * @param declaringType
	 *            the declaring type, if it is <code>null</code>, the attribute
	 *            type will not be determined
	 * @param name
	 *            the attribute name
	 * @param element
	 *            the element defining the attribute
	 * @param attributeType
	 *            the attribute type definition
	 */
	public SchemaTypeAttribute(TypeDefinition declaringType, String name,
			XmlSchemaElement element, TypeDefinition attributeType) {
		super(declaringType, name, attributeType.getName(), element);

		if (declaringType != null) {
			// set the declaring type
			declaringType.addDeclaredAttribute(this);

			// set attribute type
			setAttributeType(attributeType);
		}
	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            the schema attribute to copy
	 */
	protected SchemaTypeAttribute(SchemaTypeAttribute other) {
		super(other);
	}

	/**
	 * @see AttributeDefinition#copyAttribute(TypeDefinition)
	 */
	@Override
	public AttributeDefinition copyAttribute(TypeDefinition parentType) {
		SchemaTypeAttribute copy = new SchemaTypeAttribute(this);
		copy.setParentType(parentType);
		return copy;
	}

}
