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

package eu.esdihumboldt.hale.common.align.model;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Associates an instance context with a child definition.
 * 
 * @author Simon Templer
 */
@Immutable
public class ChildContext {

	private final Integer contextName;

	private final Integer index;

	private final Condition condition;

	private final ChildDefinition<?> child;

	/**
	 * Create a child with the default context
	 * 
	 * @param child the child definition
	 */
	public ChildContext(ChildDefinition<?> child) {
		this(null, null, null, child);
	}

	/**
	 * Create a child context.
	 * 
	 * @param contextName the instance context name, may be <code>null</code>
	 * @param index the context index, may be <code>null</code>
	 * @param condition the context condition, may be <code>null</code>
	 * @param child the child definition
	 */
	public ChildContext(Integer contextName, Integer index, Condition condition,
			ChildDefinition<?> child) {
		super();
		this.contextName = contextName;
		this.child = child;
		this.index = index;
		this.condition = condition;
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
	 * @return the index
	 */
	public Integer getIndex() {
		return index;
	}

	/**
	 * @return the condition
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((child == null) ? 0 : child.hashCode());
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((contextName == null) ? 0 : contextName.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
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
		}
		else if (!child.equals(other.child))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		}
		else if (!condition.equals(other.condition))
			return false;
		if (contextName == null) {
			if (other.contextName != null)
				return false;
		}
		else if (!contextName.equals(other.contextName))
			return false;
		if (index == null) {
			if (other.index != null)
				return false;
		}
		else if (!index.equals(other.index))
			return false;
		return true;
	}

}
