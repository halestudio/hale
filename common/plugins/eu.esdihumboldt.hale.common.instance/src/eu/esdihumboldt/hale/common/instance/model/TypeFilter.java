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

package eu.esdihumboldt.hale.common.instance.model;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Filter that matches instances with a certain associated type.
 * 
 * @author Simon Templer
 */
public class TypeFilter implements Filter {

	private final TypeDefinition type;

	/**
	 * Create a filter matching instances associated with the given type.
	 * 
	 * @param type the type definition to match, if <code>null</code> any type
	 *            associated with an instance will be a match
	 */
	public TypeFilter(TypeDefinition type) {
		super();
		this.type = type;
	}

	/**
	 * @see Filter#match(Instance)
	 */
	@Override
	public boolean match(Instance instance) {
		if (type == null) {
			return true;
		}
		else {
			return type.equals(instance.getDefinition());
		}
	}

	/**
	 * @return the type instance will be selected from
	 */
	public TypeDefinition getType() {
		return type;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TypeFilter other = (TypeFilter) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		return true;
	}

}
