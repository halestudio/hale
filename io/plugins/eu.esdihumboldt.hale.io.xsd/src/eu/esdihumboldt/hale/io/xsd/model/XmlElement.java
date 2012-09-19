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
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;

/**
 * Represents a XML schema element
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class XmlElement extends AbstractDefinition<TypeConstraint> {

	/**
	 * The element type
	 */
	private final TypeDefinition type;

	/**
	 * The substitution group
	 */
	private final QName substitutionGroup;

	/**
	 * Create a new schema element
	 * 
	 * @param elementName the element name
	 * @param type the associated type definition
	 * @param substitutionGroup the substitution group, may be <code>null</code>
	 */
	public XmlElement(QName elementName, TypeDefinition type, QName substitutionGroup) {
		super(elementName);

		// TODO also remember index for resolving the substituted element?
		// XXX what about the other direction - finding possible substitutions

		this.type = type;
		this.substitutionGroup = substitutionGroup;
	}

	/**
	 * Get the type definition associated with the element
	 * 
	 * @return the element type
	 */
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * Get the element substitution group
	 * 
	 * @return the substitution group or <code>null</code>
	 */
	public QName getSubstitutionGroup() {
		return substitutionGroup;
	}

	/**
	 * @see Definition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return getName().getNamespaceURI() + "/" + getName().getLocalPart();
	}

}
