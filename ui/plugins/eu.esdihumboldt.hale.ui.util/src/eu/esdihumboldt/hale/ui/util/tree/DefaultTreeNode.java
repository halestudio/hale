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
