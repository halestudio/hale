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
import eu.esdihumboldt.hale.ui.util.tree.MultiColumnTreeNodeLabelProvider;

/**
 * Task comment labels
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskCommentLabelProvider extends MultiColumnTreeNodeLabelProvider {

	/**
	 * @see MultiColumnTreeNodeLabelProvider#MultiColumnTreeNodeLabelProvider(int)
	 */
	public TaskCommentLabelProvider(int columnIndex) {
		super(columnIndex);
	}

	/**
	 * @see MultiColumnTreeNodeLabelProvider#getValueText(Object, TreeNode)
	 */
	@Override
	protected String getValueText(Object value, TreeNode node) {
		if (value instanceof ResolvedTask) {
			ResolvedTask<?> task = (ResolvedTask<?>) value;
			return task.getUserComment();
		}
		else {
			return null;
		}
	}

}
