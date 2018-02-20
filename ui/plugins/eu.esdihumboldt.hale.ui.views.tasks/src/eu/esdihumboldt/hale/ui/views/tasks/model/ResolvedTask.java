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

package eu.esdihumboldt.hale.ui.views.tasks.model;

import java.util.List;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskType.SeverityLevel;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskUserData.TaskStatus;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Task decorator that provides convenience methods for accessing the task
 *   type information
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class ResolvedTask implements Task {
	
	private final Task task;
	
	private final TaskType type;
	
	private final TaskUserData userData;

	/**
	 * Create a resolved task
	 * 
	 * @param task the task
	 * @param type the task's type
	 * @param userData the task user data, may be <code>null</code>
	 */
	public ResolvedTask(Task task, TaskType type, TaskUserData userData) {
		super();
		this.task = task;
		this.type = type;
		this.userData = userData;
	}
	
	/**
	 * Resolve a task
	 * 
	 * @param registry the task type registry
	 * @param task the task to be resolved
	 * @param userData the task user data, may be <code>null</code>
	 * 
	 * @return the resolved task or <code>null</code> if the task type could not
	 *   be resolved
	 */
	public static ResolvedTask resolveTask(TaskRegistry registry, Task task,
			TaskUserData userData) {
		TaskType type = registry.getType(task.getTypeName());
		if (type != null) {
			return new ResolvedTask(task, type, userData);
		}
		else {
			return null;
		}
	}

	/**
	 * @see Task#dispose()
	 */
	@Override
	public void dispose() {
		task.dispose();
	}

	/**
	 * @see Task#getContext()
	 */
	@Override
	public List<? extends Definition> getContext() {
		return task.getContext();
	}

	/**
	 * @see Task#getMainContext()
	 */
	@Override
	public Definition getMainContext() {
		return task.getMainContext();
	}

	/**
	 * Get the task status
	 * 
	 * @return the task status
	 */
	public TaskStatus getTaskStatus() {
		if (userData == null) {
			return TaskStatus.NEW;
		}
		else {
			return userData.getTaskStatus();
		}
	}

	/**
	 * Get the user comment
	 * 
	 * @return the user comment or <code>null</code>
	 */
	public String getUserComment() {
		if (userData == null) {
			return null;
		}
		else {
			return userData.getUserComment();
		}
	}

	/**
	 * @see Task#getTypeName()
	 */
	@Override
	public String getTypeName() {
		return task.getTypeName();
	}

	/**
	 * Get the value of the task
	 * 
	 * @return the task value
	 */
	public double getValue() {
		return type.getValue(task);
	}
	
	/**
	 * The task factory that provides tasks of this type
	 * 
	 * @return the task factory
	 */
	public TaskFactory getTaskFactory() {
		return type.getTaskFactory();
	}
	
	/**
	 * Get the severity level
	 * 
	 * @see TaskType#getSeverityLevel(Task)
	 * 
	 * @return the severity level of the task 
	 */
	public SeverityLevel getSeverityLevel() {
		return type.getSeverityLevel(task);
	}
	
	/**
	 * Get the creation reason
	 * 
	 * @see TaskType#getReason(Task)
	 * 
	 * @return the task's creation reason
	 */
	public String getReason() {
		return type.getReason(task);
	}
	
	/**
	 * Get the task title
	 * 
	 * @see TaskType#getTitle(Task)
	 * 
	 * @return the task title
	 */
	public String getTitle() {
		return type.getTitle(task);
	}

	/**
	 * Get the task type
	 * 
	 * @return the task type
	 */
	public TaskType getType() {
		return type;
	}

	/**
	 * @see Task#setTaskService(TaskService)
	 */
	@Override
	public void setTaskService(TaskService taskService) {
		task.setTaskService(taskService);
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Task other) {
		if (other instanceof ResolvedTask) {
			int result = getSeverityLevel().compareTo(((ResolvedTask) other).getSeverityLevel());
			
			if (result == 0) {
				if (getValue() > ((ResolvedTask) other).getValue()) {
					return -1;
				}
				else if (getValue() < ((ResolvedTask) other).getValue()) {
					return 1;
				}
			}
			else {
				return result;
			}
		}
		
		return task.compareTo(other);
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @return the userData
	 */
	public TaskUserData getUserData() {
		return userData;
	}
	
	/**
	 * Determines if this is an open task
	 * 
	 * @return if this is an open task
	 */
	public boolean isOpen() {
		TaskStatus status = getTaskStatus();
		switch (status) {
		case COMPLETED: // fall through
		case IGNORED:
			return false;
		case ACTIVE: // fall through
		case NEW: // fall through
		default:
			return true;
		}
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return task.hashCode();
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Task))
			return false;
		Task other = (Task) obj;
		if (!task.equals(other))
			return false;
		return true;
	}

}
