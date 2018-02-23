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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.tasks.ResolvedTask;
import eu.esdihumboldt.hale.common.tasks.TaskService;
import eu.esdihumboldt.hale.common.tasks.TaskUserData;

/**
 * Editing support for the task comment
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskCommentEditingSupport extends AbstractTaskEditingSupport {

	/**
	 * Constructor
	 * 
	 * @param viewer the column viewer
	 * @param taskService the task service
	 */
	public TaskCommentEditingSupport(ColumnViewer viewer, TaskService taskService) {
		super(viewer, taskService);
	}

	/**
	 * @see EditingSupport#getCellEditor(Object)
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor((Composite) getViewer().getControl());
	}

	/**
	 * @see EditingSupport#getValue(Object)
	 */
	@Override
	protected Object getValue(Object element) {
		ResolvedTask task = getTask(element);

		Object value = null;
		if (task != null) {
			value = task.getUserComment();
		}

		if (value == null) {
			// may not return null as value
			return ""; //$NON-NLS-1$
		}
		else {
			return value;
		}
	}

	/**
	 * @see EditingSupport#setValue(Object, Object)
	 */
	@Override
	protected void setValue(Object element, Object value) {
		ResolvedTask task = getTask(element);
		if (task != null) {
			TaskUserData userData = getUserData(task);
			userData.setUserComment(value.toString());
			setUserData(task, userData);
		}
	}

}
