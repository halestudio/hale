/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
