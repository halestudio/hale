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

package eu.esdihumboldt.hale.common.align.model;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Associates an instance context with a child definition.
 * @author Simon Templer
 */
@Immutable
public class ChildContext {
	
	private final Integer contextName;
	
	private final ChildDefinition<?> child;
	
	/**
	 * Create a child with the default context
	 * @param child the child definition
	 */
	public ChildContext(ChildDefinition<?> child) {
		this(null, child);
	}

	/**
	 * Create a child context
	 * @param contextName the instance context name
	 * @param child the child definition
	 */
	public ChildContext(Integer contextName, ChildDefinition<?> child) {
		super();
		this.contextName = contextName;
		this.child = child;
	}

	/**
	 * @return the instance context name
	 */
	public Integer getContextName() {
		return contextName;
	}

	/**
	 * @return the child definition
	 */
	public ChildDefinition<?> getChild() {
		return child;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime * result
				+ ((contextName == null) ? 0 : contextName.hashCode());
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
		ChildContext other = (ChildContext) obj;
		if (child == null) {
			if (other.child != null)
				return false;
		} else if (!child.equals(other.child))
			return false;
		if (contextName == null) {
			if (other.contextName != null)
				return false;
		} else if (!contextName.equals(other.contextName))
			return false;
		return true;
	}

}
