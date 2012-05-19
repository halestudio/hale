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
public class CustomDefaultAttribute extends AbstractDefaultAttribute {

	/**
	 * Copy constructor
	 * 
	 * @param other the other attribute
	 */
	protected CustomDefaultAttribute(AbstractDefaultAttribute other) {
		super(other);
	}

	/**
	 * @see AbstractDefaultAttribute#AbstractDefaultAttribute(String, Name, TypeDefinition, String, XmlSchemaUse)
	 */
	public CustomDefaultAttribute(String name, Name typeName,
			TypeDefinition attributeType, String namespace, XmlSchemaUse use) {
		super(name, typeName, attributeType, namespace, use);
	}

	/**
	 * @see AttributeDefinition#copyAttribute(TypeDefinition)
	 */
	@Override
	public AttributeDefinition copyAttribute(TypeDefinition parentType) {
		CustomDefaultAttribute copy = new CustomDefaultAttribute(this);
		copy.setParentType(parentType);
		return copy;
	}

}
