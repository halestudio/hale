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

package eu.esdihumboldt.hale.rcp.views.tasks;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.utils.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MapTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNodeLabelProvider;
import eu.esdihumboldt.hale.task.ResolvedTask;

/**
 * Task description label provider
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskValueLabelProvider extends MultiColumnTreeNodeLabelProvider {
	
	private final Image priorityImage;
	
	private final int index;
	
	/**
	 * Default constructor
	 * 
	 * @param index index of the task value 
	 */
	public TaskValueLabelProvider(int index) {
		super(index);
		
		this.index = index;
		
		priorityImage = HALEActivator.getImageDescriptor("icons/priority.gif").createImage();
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getValueImage(Object, TreeNode)
	 */
	@Override
	protected Image getValueImage(Object value, TreeNode node) {
		if (value instanceof ResolvedTask) {
			ResolvedTask task = (ResolvedTask) value;
			return getValueImage(task.getValue());
		}
		else if (node instanceof MapTreeNode<?, ?>) {
			return getNodeImage((MapTreeNode<?, ?>) node);
		}
		else {
			return super.getValueImage(value, node);
		}
	}

	/**
	 * Get the image for the given value
	 * 
	 * @param value the value
	 * 
	 * @return the image or <code>null</code>
	 */
	private Image getValueImage(double value) {
		if (value > 0.5) {
			return priorityImage;
		}
		else {
			return null;
		}
	}

	/**
	 * Get the image for the given node
	 * 
	 * @param node the node
	 * 
	 * @return the image or <code>null</code>
	 */
	private Image getNodeImage(MapTreeNode<?, ?> node) {
		double value = getNodeValue(node);
		
		return getValueImage(value);
	}

	/**
	 * Get the given node's severity level
	 * 
	 * @param node the node
	 * 
	 * @return the severity level or <code>null</code>
	 */
	private double getNodeValue(MapTreeNode<?, ?> node) {
		double result = 0.0;
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
				// determine level
				if (value instanceof ResolvedTask) {
					ResolvedTask task = (ResolvedTask) value;
					result = Math.max(result, task.getValue());
				}
			}
			else if (child instanceof MapTreeNode<?, ?>) {
				// child is map node
				MapTreeNode<?, ?> childNode = (MapTreeNode<?, ?>) child;
				double childValue = getNodeValue(childNode);
				result = Math.max(result, childValue);
			}
		}
		
		return result;
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getValueText(Object, TreeNode)
	 */
	@Override
	protected String getValueText(Object value, TreeNode node) {
		return "";
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		priorityImage.dispose();
		
		super.dispose();
	}

}
