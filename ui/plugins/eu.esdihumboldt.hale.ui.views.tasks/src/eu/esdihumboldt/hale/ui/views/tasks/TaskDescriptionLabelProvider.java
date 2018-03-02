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

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.tasks.ResolvedTask;
import eu.esdihumboldt.hale.common.tasks.TaskType.TaskSeverity;
import eu.esdihumboldt.hale.ui.util.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.ui.util.tree.MapTreeNode;
import eu.esdihumboldt.hale.ui.util.tree.MultiColumnTreeNodeLabelProvider;
import eu.esdihumboldt.hale.ui.views.tasks.internal.TasksViewPlugin;

/**
 * Task description label provider
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskDescriptionLabelProvider extends MultiColumnTreeNodeLabelProvider {

	private final Image taskImage;

	private final int index;

	/**
	 * Default constructor
	 * 
	 * @param index index of the task value
	 */
	public TaskDescriptionLabelProvider(int index) {
		super(index);

		this.index = index;

		taskImage = TasksViewPlugin.getImageDescriptor("icons/tasks.gif").createImage(); //$NON-NLS-1$
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getValueImage(Object, TreeNode)
	 */
	@Override
	protected Image getValueImage(Object value, TreeNode node) {
		if (value instanceof ResolvedTask<?>) {
			ResolvedTask<?> task = (ResolvedTask<?>) value;
			return getLevelImage(task.getSeverityLevel());
		}
		else if (node instanceof MapTreeNode<?, ?>) {
			return getNodeImage((MapTreeNode<?, ?>) node);
		}
		else {
			return super.getValueImage(value, node);
		}
	}

	/**
	 * Get the image for the given severity level
	 * 
	 * @param severityLevel the severity level
	 * 
	 * @return the image or <code>null</code>
	 */
	private Image getLevelImage(TaskSeverity severityLevel) {
		if (severityLevel == null) {
			return null;
		}

		switch (severityLevel) {
		case TASK:
			// return taskImage;
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		case WARNING:
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		case ERROR:
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		default:
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
		TaskSeverity level = getNodeLevel(node);

		return getLevelImage(level);
	}

	/**
	 * Get the given node's severity level
	 * 
	 * @param node the node
	 * 
	 * @return the severity level or <code>null</code>
	 */
	private TaskSeverity getNodeLevel(MapTreeNode<?, ?> node) {
		TaskSeverity level = null;
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
					ResolvedTask<?> task = (ResolvedTask<?>) value;
					if (task.isOpen()) { // only inspect open tasks
						level = TaskSeverity.max(level, task.getSeverityLevel());
					}
				}
			}
			else if (child instanceof MapTreeNode<?, ?>) {
				// child is map node
				MapTreeNode<?, ?> childNode = (MapTreeNode<?, ?>) child;
				TaskSeverity childLevel = getNodeLevel(childNode);
				level = TaskSeverity.max(level, childLevel);
			}
		}

		return level;
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getValueText(Object, TreeNode)
	 */
	@Override
	protected String getValueText(Object value, TreeNode node) {
		if (value instanceof ResolvedTask) {
			// task title
			return ((ResolvedTask<?>) value).getTitle();
		}
		else {
			return super.getValueText(value, node);
		}
	}

	/**
	 * @see BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		taskImage.dispose();

		super.dispose();
	}

}
