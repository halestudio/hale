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

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;

import eu.esdihumboldt.hale.common.tasks.ResolvedTask;
import eu.esdihumboldt.hale.common.tasks.TaskService;
import eu.esdihumboldt.hale.common.tasks.TaskUserData;
import eu.esdihumboldt.hale.common.tasks.TaskUserDataAware;
import eu.esdihumboldt.hale.common.tasks.TaskUserDataImpl;
import eu.esdihumboldt.hale.ui.util.tree.AbstractMultiColumnTreeNode;

/**
 * Editing support for the tasks
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTaskEditingSupport extends EditingSupport {

	private final TaskService taskService;

	/**
	 * Constructor
	 * 
	 * @param viewer the column viewer
	 * @param taskService the task service
	 */
	public AbstractTaskEditingSupport(ColumnViewer viewer, TaskService taskService) {
		super(viewer);

		this.taskService = taskService;
	}

	/**
	 * @see EditingSupport#canEdit(Object)
	 */
	@Override
	protected boolean canEdit(Object element) {
		return getTask(element) != null;
	}

	/**
	 * Get the element's resolved task if possible
	 * 
	 * @param element the element
	 * 
	 * @return the resolved task or <code>null</code>
	 */
	protected ResolvedTask<?> getTask(Object element) {
		if (element instanceof AbstractMultiColumnTreeNode) {
			Object value = ((AbstractMultiColumnTreeNode) element).getFirstValue();
			if (value instanceof ResolvedTask) {
				return (ResolvedTask<?>) value;
			}
		}

		return null;
	}

	/**
	 * Set the given task's user data
	 * 
	 * @param task the task
	 * @param userData the user data
	 */
	protected void setUserData(ResolvedTask<?> task, TaskUserData userData) {
		taskService.setUserData(task.getTask(), userData);
	}

	/**
	 * Get the user data for the given task
	 * 
	 * @param task the task
	 * 
	 * @return the user data
	 */
	protected TaskUserData getUserData(ResolvedTask<?> task) {
		TaskUserData userData = task.getUserData();
		if (userData == null) {
			userData = new TaskUserDataImpl();
			if (task.getTask() instanceof TaskUserDataAware) {
				TaskUserDataAware tuda = (TaskUserDataAware) task.getTask();
				tuda.populateUserData(userData);
			}
		}
		return userData;
	}

}
