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
package eu.esdihumboldt.hale.common.tasks;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The {@link TaskService} provides information on all Tasks that have been
 * created for the current Alignment project. It stores tasks in all kinds of
 * statuses, including completed and obsolete ones.
 * 
 * Note that if during construction of this service no Comparator&lt;Task&gt; is
 * specified, all {@link List}s that are returned are sorted by the {@link Task}
 * value, in descending order (highest value first).
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TaskService {

	// TODO set active task etc?

	/**
	 * Add a task
	 * 
	 * @param task the task to add
	 */
	void addTask(Task<?> task);

	/**
	 * Add tasks
	 * 
	 * @param tasks the tasks to add
	 */
	<C> void addTasks(Iterable<Task<C>> tasks);

	/**
	 * Remove a task
	 * 
	 * @param task the task to remove
	 */
	void removeTask(Task<?> task);

	/**
	 * Remove all tasks of the given type
	 * 
	 * @param type the type name
	 */
	void removeTasks(String type);

	/**
	 * Set the user data for the given task
	 * 
	 * @param task the task
	 * @param userData the task user data
	 */
	void setUserData(Task<?> task, TaskUserData userData);

	/**
	 * Get the tasks
	 * 
	 * @return a collection of all tasks
	 */
	Collection<Task<?>> getTasks();

	/**
	 * Resolves the tasks and returns them
	 * 
	 * @return the resolved tasks
	 */
	Collection<ResolvedTask<?>> getResolvedTasks();

	/**
	 * Resolves the given task
	 * 
	 * @param task the task to resolve
	 * 
	 * @return the resolved task
	 */
	ResolvedTask<?> resolveTask(Task<?> task);

	// task provider management

	/**
	 * Activate the task provider with the given ID
	 * 
	 * @param id the task provider id
	 */
	void activateTaskProvider(String id);

	/**
	 * Get the user tasks
	 * 
	 * @return the user tasks
	 */
	Map<Task<?>, TaskUserData> getUserTasks();

	/**
	 * Clear the user data associated with the tasks
	 */
	void clearUserTasks();

	/**
	 * Clear all tasks
	 */
	void clearTasks();

	/**
	 * Adds a listener to the service
	 * 
	 * @param listener the listener to add
	 */
	void addListener(TaskServiceListener listener);

	/**
	 * Removes a listener to the service
	 * 
	 * @param listener the listener to remove
	 */
	void removeListener(TaskServiceListener listener);
}
