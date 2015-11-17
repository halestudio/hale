/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.model.impl;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Decorator for a {@link PropertyDefinition} that overrides the property type.
 * 
 * @author Simon Templer
 */
public class PropertyTypeOverrideProperty extends AbstractPropertyDecorator {

	private final TypeDefinition newPropertyType;

	/**
	 * Constructor.
	 * 
	 * @param property the original property
	 * @param newPropertyType the replacement property type
	 */
	public PropertyTypeOverrideProperty(PropertyDefinition property, TypeDefinition newPropertyType) {
		super(property);
		this.newPropertyType = newPropertyType;
	}

	@Override
	public TypeDefinition getPropertyType() {
		return newPropertyType;
	}

}
