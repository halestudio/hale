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

package eu.esdihumboldt.hale.common.tasks;

import java.util.List;

import eu.esdihumboldt.hale.common.tasks.TaskType.TaskSeverity;
import eu.esdihumboldt.hale.common.tasks.TaskUserData.TaskStatus;

/**
 * Task decorator that provides convenience methods for accessing the task type
 * information
 * 
 * @param <C> The type of the context object
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ResolvedTask<C> implements Task<C> {

	private final Task<C> task;

	private final TaskUserData userData;

	/**
	 * Create a resolved task
	 * 
	 * @param task the task
	 * @param userData the task user data, may be <code>null</code>
	 */
	public ResolvedTask(Task<C> task, TaskUserData userData) {
		super();
		this.task = task;
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
	 *         be resolved
	 */
	public static <C> ResolvedTask<C> resolveTask(TaskRegistry registry, Task<C> task,
			TaskUserData userData) {
		if (userData == null) {
			userData = new TaskUserDataImpl();
		}

		if (task instanceof TaskUserDataAware) {
			((TaskUserDataAware) task).populateUserData(userData);
		}

		return new ResolvedTask<C>(task, userData);
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
	public List<? extends C> getContext() {
		return task.getContext();
	}

	/**
	 * @see Task#getMainContext()
	 */
	@Override
	public C getMainContext() {
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

	@Override
	public TaskType<C> getTaskType() {
		return task.getTaskType();
	}

	/**
	 * The task factory that provides tasks of this type
	 * 
	 * @return the task factory
	 */
	public TaskFactory<C> getTaskFactory() {
		return task.getTaskType().getTaskFactory();
	}

	/**
	 * Get the severity level
	 * 
	 * @see TaskType#getSeverityLevel(Task)
	 * 
	 * @return the severity level of the task
	 */
	public TaskSeverity getSeverityLevel() {
		return task.getTaskType().getSeverityLevel(task);
	}

	/**
	 * Get the creation reason
	 * 
	 * @see TaskType#getReason(Task)
	 * 
	 * @return the task's creation reason
	 */
	public String getReason() {
		return task.getTaskType().getReason(task);
	}

	/**
	 * Get the task title
	 * 
	 * @see TaskType#getTitle(Task)
	 * 
	 * @return the task title
	 */
	public String getTitle() {
		return task.getTaskType().getTitle(task);
	}

	/**
	 * Get the task type
	 * 
	 * @return the task type
	 */
	public TaskType<C> getType() {
		return task.getTaskType();
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Task<C> other) {
		return task.compareTo(other);
	}

	/**
	 * @return the task
	 */
	public Task<C> getTask() {
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
//		case IGNORED:
			return false;
//		case ACTIVE: // fall through
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
		Task<?> other = (Task<?>) obj;
		if (!task.equals(other))
			return false;
		return true;
	}

}
