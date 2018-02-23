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

package eu.esdihumboldt.hale.ui.views.tasks;

import org.eclipse.jface.viewers.TreeNode;

import eu.esdihumboldt.hale.common.tasks.ResolvedTask;
import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.ui.util.tree.MapTreeNode;
import eu.esdihumboldt.hale.ui.util.tree.MultiColumnTreeNodeLabelProvider;

/**
 * Task description label provider
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskCountLabelProvider extends MultiColumnTreeNodeLabelProvider {

	private final int index;

	/**
	 * Default constructor
	 * 
	 * @param index index of the task value
	 */
	public TaskCountLabelProvider(int index) {
		super(index);

		this.index = index;
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getValueText(Object, TreeNode)
	 */
	@Override
	protected String getValueText(Object value, TreeNode node) {
		if (value instanceof ResolvedTask) {
			return null;
		}
		else if (node instanceof MapTreeNode<?, ?>) {
			return getNodeText((MapTreeNode<?, ?>) node);
		}
		else {
			return super.getValueText(value, node);
		}
	}

	/**
	 * Get the image for the given node
	 * 
	 * @param node the node
	 * 
	 * @return the image or <code>null</code>
	 */
	private String getNodeText(MapTreeNode<?, ?> node) {
		int count = getTaskCount(node);

		return String.valueOf(count);
	}

	/**
	 * Get the given node's task count
	 * 
	 * @param node the node
	 * 
	 * @return the task count
	 */
	private int getTaskCount(MapTreeNode<?, ?> node) {
		int count = 0;
		TreeNode[] children = node.getChildren();

		for (TreeNode child : children) {
			if (child instanceof DefaultTreeNode) {
				// child is task node
				DefaultTreeNode childNode = (DefaultTreeNode) child;
				Object tmp = childNode.getValue();
				// get value
				Object value;
				if (tmp.getClass().isArray()) {
					value = ((Object[]) tmp)[index];
				}
				else {
					value = tmp;
				}
				// add task to count if it is open
				if (value instanceof ResolvedTask && ((ResolvedTask) value).isOpen()) {
					count++;
				}
			}
			else if (child instanceof MapTreeNode<?, ?>) {
				// child is map node
				MapTreeNode<?, ?> childNode = (MapTreeNode<?, ?>) child;
				count += getTaskCount(childNode);
			}
		}

		return count;
	}

}
