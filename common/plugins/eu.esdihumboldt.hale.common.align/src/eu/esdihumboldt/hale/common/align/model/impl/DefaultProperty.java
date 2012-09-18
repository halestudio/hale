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

package eu.esdihumboldt.hale.common.align.model.impl;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.Property;

/**
 * Represents a property in a mapping cell
 * 
 * @author Simon Templer
 */
public class DefaultProperty implements Property {

	private final PropertyEntityDefinition definition;

	/**
	 * Create a property entity
	 * 
	 * @param definition the property entity definition
	 */
	public DefaultProperty(PropertyEntityDefinition definition) {
		super();
		this.definition = definition;
	}

	/**
	 * @see Entity#getDefinition()
	 */
	@Override
	public PropertyEntityDefinition getDefinition() {
		return definition;
	}

	// TODO type filter/restriction stuff - also update hashCode/equals?!

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((definition == null) ? 0 : definition.hashCode());
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
		DefaultProperty other = (DefaultProperty) obj;
		if (definition == null) {
			if (other.definition != null)
				return false;
		}
		else if (!definition.equals(other.definition))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getDefinition().toString();
	}

}
