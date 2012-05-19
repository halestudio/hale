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
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@Deprecated
public class ElementReferenceAttribute extends AbstractElementAttribute {
	
	private final SchemaElement reference;

	/**
	 * Constructor
	 * 
	 * @param declaringType the declaring type
	 * @param name the element name
	 * @param typeName the type name
	 * @param element the XML element
	 * @param reference the reference to the schema element
	 */
	public ElementReferenceAttribute(TypeDefinition declaringType, String name,
			Name typeName, XmlSchemaElement element, SchemaElement reference) {
		super(declaringType, name, typeName, element);
		
		this.reference = reference;
	}

	/**
	 * Copy constructor
	 * 
	 * @param other the attribute top copy
	 */
	protected ElementReferenceAttribute(ElementReferenceAttribute other) {
		super(other);
		
		reference = other.reference;
	}

	/**
	 * @see AttributeDefinition#getDefaultAttributeType()
	 */
	@Override
	protected TypeDefinition getDefaultAttributeType() {
		return reference.getType();
	}
	
	/**
	 * @see AttributeDefinition#copyAttribute(TypeDefinition)
	 */
	@Override
	public AttributeDefinition copyAttribute(TypeDefinition parentType) {
		ElementReferenceAttribute copy = new ElementReferenceAttribute(this);
		copy.setParentType(parentType);
		return copy;
	}

	/**
	 * @return the reference
	 */
	public SchemaElement getReference() {
		return reference;
	}

}
