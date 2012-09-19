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

package eu.esdihumboldt.hale.ui.util.tree;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeNode;

/**
 * Tree node that stores values for multiple columns
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public abstract class AbstractMultiColumnTreeNode extends TreeNode {

	private Object value;

	/**
	 * Create a new node
	 * 
	 * @param values the node values
	 */
	public AbstractMultiColumnTreeNode(Object... values) {
		super(values);

		this.value = values;
	}

	/**
	 * @see TreeNode#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * Set the node value
	 * 
	 * @param values the node values
	 */
	public void setValues(Object... values) {
		this.value = values;
	}

	/**
	 * Get the child nodes
	 * 
	 * @return a collection of child nodes, it should never be changed
	 */
	protected abstract Collection<? extends TreeNode> getChildNodes();

	/**
	 * @see TreeNode#getChildren()
	 */
	@Override
	public TreeNode[] getChildren() {
		Collection<? extends TreeNode> children = getChildNodes();
		return children.toArray(new TreeNode[children.size()]);
	}

	/**
	 * @see TreeNode#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !getChildNodes().isEmpty();
	}

	/**
	 * Get the first value
	 * 
	 * @return the first value
	 */
	public Object getFirstValue() {
		Object tmp = getValue();
		if (tmp == null) {
			return null;
		}
		else if (tmp.getClass().isArray()) {
			if (((Object[]) tmp).length > 0) {
				return ((Object[]) tmp)[0];
			}
			else {
				return null;
			}
		}
		else {
			return tmp;
		}
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((getChildNodes() == null) ? 0 : getChildNodes().hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractMultiColumnTreeNode other = (AbstractMultiColumnTreeNode) obj;
		if (getChildNodes() == null) {
			if (other.getChildNodes() != null)
				return false;
		}
		else if (!getChildNodes().equals(other.getChildNodes()))
			return false;
		return true;
	}

}
