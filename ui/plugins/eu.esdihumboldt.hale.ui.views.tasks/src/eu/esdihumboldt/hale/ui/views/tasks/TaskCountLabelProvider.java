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

package eu.esdihumboldt.hale.ui.views.tasks;

import org.eclipse.jface.viewers.TreeNode;

import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.ui.util.tree.MapTreeNode;
import eu.esdihumboldt.hale.ui.util.tree.MultiColumnTreeNodeLabelProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;

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
