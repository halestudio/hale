/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.filter;

import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Filter that matches instances with a certain associated type.
 * @author Simon Templer
 */
public class TypeFilter implements Filter {
	
	private final TypeDefinition type;

	/**
	 * Create a filter matching instances associated with the given type.
	 * @param type the type definition to match, if <code>null</code> any type
	 *   associated with an instance will be a match
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
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
