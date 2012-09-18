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

package eu.esdihumboldt.hale.ui.views.tasks.service.internal;

import eu.esdihumboldt.hale.ui.service.AbstractUpdateService;
import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskServiceListener;

/**
 * Notification handling for {@link TaskService}s that support
 * {@link TaskServiceListener}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTaskService extends AbstractUpdateService
		implements TaskService {

	/**
	 * The default update message
	 */
	private static final UpdateMessage<?> DEF_MESSAGE = new UpdateMessage<Object>(TaskService.class, null);
	
	/**
	 * @see AbstractUpdateService#notifyListeners(UpdateMessage)
	 * @deprecated an {@link UnsupportedOperationException} will be thrown,
	 *   use another notification method instead
	 */
	@Deprecated
	@Override
	protected void notifyListeners(UpdateMessage<?> message) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Call when tasks have been added
	 * 
	 * @param tasks the tasks that have been added
	 */
	protected void notifyTasksAdded(Iterable<Task> tasks) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof TaskServiceListener) {
				((TaskServiceListener) listener).tasksAdded(tasks);
			}
			
			listener.update(DEF_MESSAGE);
		}
	}
	
	/**
	 * Call when tasks have been removed
	 * 
	 * @param tasks the tasks that have been removed
	 */
	protected void notifyTasksRemoved(Iterable<Task> tasks) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof TaskServiceListener) {
				((TaskServiceListener) listener).tasksRemoved(tasks);
			}
			
			listener.update(DEF_MESSAGE);
		}
	}
	
	/**
	 * Call when the user data of a task has changed
	 * 
	 * @param task the resolved task
	 */
	protected void notifyTaskUserDataChanged(ResolvedTask task) {
		for (HaleServiceListener listener : getListeners()) {
			if (listener instanceof TaskServiceListener) {
				((TaskServiceListener) listener).taskUserDataChanged(task);
			}
			
			listener.update(DEF_MESSAGE);
		}
	}

}
