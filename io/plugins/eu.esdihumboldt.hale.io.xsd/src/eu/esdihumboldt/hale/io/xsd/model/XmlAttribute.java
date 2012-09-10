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

package eu.esdihumboldt.hale.io.xsd.model;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;

/**
 * Represents a XML schema attribute
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlAttribute extends AbstractDefinition<PropertyConstraint> {

	/**
	 * The attribute type
	 */
	private final TypeDefinition type;

	/**
	 * Create a new schema attribute
	 * 
	 * @param attributeName the attribute name
	 * @param type the associated type definition
	 */
	public XmlAttribute(QName attributeName, TypeDefinition type) {
		super(attributeName);

		this.type = type;

		// TODO set schema element constraint on type
	}

	/**
	 * Get the type definition associated with the attribute
	 * 
	 * @return the element type
	 */
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getName().getNamespaceURI() + "/" + getName().getLocalPart();
	}

}
