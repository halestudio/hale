/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.utils.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeNode;

/**
 * Tree node that stores values for multiple columns
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MultiColumnTreeNode extends TreeNode {
	
	private final List<TreeNode> children = new ArrayList<TreeNode>();

	/**
	 * Create a new node
	 * 
	 * @param values the node values
	 */
	public MultiColumnTreeNode(Object... values) {
		super(values);
	}
	
	/**
	 * Add a child to the node
	 * 
	 * @param child the child node
	 */
	public void addChild(TreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	/**
	 * @see TreeNode#getChildren()
	 */
	@Override
	public TreeNode[] getChildren() {
		return children.toArray(new TreeNode[children.size()]);
	}

	/**
	 * @see TreeNode#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/**
	 * @see TreeNode#setChildren(TreeNode[])
	 */
	@Override
	public void setChildren(TreeNode[] children) {
		this.children.clear();
		for (TreeNode node : children) {
			this.children.add(node);
		}
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
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
		MultiColumnTreeNode other = (MultiColumnTreeNode) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		return true;
	}

}
