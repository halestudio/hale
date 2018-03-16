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
 *     
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.ui.service.tasks.internal;

import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.tasks.ResolvedTask;
import eu.esdihumboldt.hale.common.tasks.Task;
import eu.esdihumboldt.hale.common.tasks.TaskService;
import eu.esdihumboldt.hale.common.tasks.TaskServiceListener;

/**
 * Notification handling for {@link TaskService}s that support
 * {@link TaskServiceListener}s
 *
 * @author Simon Templer
 * @author Florian Esser
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTaskService implements TaskService {

	private final CopyOnWriteArraySet<TaskServiceListener> listeners = new CopyOnWriteArraySet<>();

	@Override
	public void addListener(TaskServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(TaskServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Call when tasks have been added
	 * 
	 * @param tasks the tasks that have been added
	 */
	protected <C> void notifyTasksAdded(Iterable<Task<C>> tasks) {
		for (TaskServiceListener listener : listeners) {
			listener.tasksAdded(tasks);
		}
	}

	/**
	 * Call when tasks have been removed
	 * 
	 * @param tasks the tasks that have been removed
	 */
	protected void notifyTasksRemoved(Iterable<Task<?>> tasks) {
		for (TaskServiceListener listener : listeners) {
			listener.tasksRemoved(tasks);
		}
	}

	/**
	 * Call when the user data of a task has changed
	 * 
	 * @param task the resolved task
	 */
	protected void notifyTaskUserDataChanged(ResolvedTask<?> task) {
		for (TaskServiceListener listener : listeners) {
			listener.taskUserDataChanged(task);
		}
	}

}
