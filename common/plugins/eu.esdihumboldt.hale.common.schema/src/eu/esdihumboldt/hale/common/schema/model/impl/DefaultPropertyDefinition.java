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

package eu.esdihumboldt.hale.common.schema.model.impl;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default {@link PropertyDefinition} implementation
 * 
 * @author Simon Templer
 */
public class DefaultPropertyDefinition extends AbstractChildDefinition<PropertyConstraint>
		implements PropertyDefinition {

	/**
	 * The type associated with the property content
	 */
	private final TypeDefinition propertyType;

	/**
	 * Create a new property and add it to the parent group
	 * 
	 * @param name the property qualified name
	 * @param parentGroup the parent group
	 * @param propertyType the property type
	 */
	public DefaultPropertyDefinition(QName name, DefinitionGroup parentGroup,
			TypeDefinition propertyType) {
		super(name, parentGroup);
		this.propertyType = propertyType;
	}

	/**
	 * @see PropertyDefinition#getPropertyType()
	 */
	@Override
	public TypeDefinition getPropertyType() {
		return propertyType;
	}

	/**
	 * @see AbstractDefinition#getDescription()
	 */
	@Override
	public String getDescription() {
		String desc = super.getDescription();
		if (desc == null || desc.isEmpty()) {
			return getPropertyType().getDescription();
		}
		return desc;
	}

	/**
	 * @see AbstractDefinition#toString()
	 */
	@Override
	public String toString() {
		return "[property] " + super.toString();
	}

	/**
	 * @see ChildDefinition#asProperty()
	 */
	@Override
	public PropertyDefinition asProperty() {
		return this;
	}

	/**
	 * @see ChildDefinition#asGroup()
	 */
	@Override
	public GroupPropertyDefinition asGroup() {
		return null;
	}

}
