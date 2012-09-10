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
public class DefaultAttribute extends AbstractDefaultAttribute {

	/**
	 * Create a default attribute
	 * 
	 * @param declaringType
	 *            the declaring type
	 * @param typeName
	 *            the attribute type name
	 * @param attribute
	 *            the attribute
	 * @param attributeType
	 *            the attribute type
	 * @param use
	 *            the attribute use
	 */
	public DefaultAttribute(TypeDefinition declaringType, Name typeName,
			XmlSchemaAttribute attribute, TypeDefinition attributeType,
			XmlSchemaUse use) {
		super(attribute.getName(), typeName, attributeType,
				getNamespace(attribute), use);

		String description = AbstractElementAttribute.getDescription(attribute);
		if (description != null) {
			setDescription(description);
		}

		if (declaringType != null) {
			// set the declaring type
			declaringType.addDeclaredAttribute(this);
		}
	}

	private static String getNamespace(XmlSchemaAttribute attribute) {
		if (attribute.getQName() != null) {
			return attribute.getQName().getNamespaceURI();
		} else if (attribute.getRefName() != null) {
			return attribute.getRefName().getNamespaceURI();
		} else {
			return null;
		}
	}

	/**
	 * Copy constructor
	 * 
	 * @param other
	 *            the attribute to copy
	 */
	protected DefaultAttribute(DefaultAttribute other) {
		super(other);
	}

	/**
	 * @see AttributeDefinition#copyAttribute(TypeDefinition)
	 */
	@Override
	public AttributeDefinition copyAttribute(TypeDefinition parentType) {
		DefaultAttribute copy = new DefaultAttribute(this);
		copy.setParentType(parentType);
		return copy;
	}

}
