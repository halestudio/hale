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

package eu.esdihumboldt.hale.ui.util.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeNode;

/**
 * Tree node that stores values for multiple columns
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: MultiColumnTreeNode.java 2510 2010-01-21 08:49:00Z stempler $
 */
public class DefaultTreeNode extends AbstractMultiColumnTreeNode {

	private final List<TreeNode> children = new ArrayList<TreeNode>();

	/**
	 * Create a new node
	 * 
	 * @param values the node values
	 */
	public DefaultTreeNode(Object... values) {
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
	 * @see TreeNode#setChildren(TreeNode[])
	 */
	@Override
	public void setChildren(TreeNode[] children) {
		this.children.clear();
		for (TreeNode node : children) {
			addChild(node);
		}
	}

	/**
	 * @see AbstractMultiColumnTreeNode#getChildNodes()
	 */
	@Override
	protected Collection<TreeNode> getChildNodes() {
		return children;
	}

}
