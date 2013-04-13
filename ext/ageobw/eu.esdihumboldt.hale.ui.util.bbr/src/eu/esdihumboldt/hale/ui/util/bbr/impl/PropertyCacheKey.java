/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.ui.util.bbr.impl;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Key for property definitions where also an equal parent type should be
 * checked.
 * 
 * @author Simon Templer
 */
class PropertyCacheKey {

	private final PropertyDefinition property;
	private final TypeDefinition parentType;

	/**
	 * Create a property wrapper that for equality also checks the parent type.
	 * 
	 * @param property the property definition
	 */
	public PropertyCacheKey(PropertyDefinition property) {
		super();
		this.property = property;
		this.parentType = property.getParentType();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parentType == null) ? 0 : parentType.hashCode());
		result = prime * result + ((property == null) ? 0 : property.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertyCacheKey other = (PropertyCacheKey) obj;
		if (parentType == null) {
			if (other.parentType != null)
				return false;
		}
		else if (!parentType.equals(other.parentType))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		}
		else if (!property.equals(other.property))
			return false;
		return true;
	}

}
