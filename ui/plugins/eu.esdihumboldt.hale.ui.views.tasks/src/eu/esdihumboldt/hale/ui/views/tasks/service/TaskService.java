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
package eu.esdihumboldt.hale.ui.views.tasks.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.hale.ui.service.UpdateService;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskUserData;

/**
 * The {@link TaskService} provides information on all Tasks that have been 
 * created for the current Alignment project. It stores tasks in all kinds of 
 * statii, including completed and obsolete ones.
 * 
 * Note that if during construction of this service no Comparator&lt;Task&gt; is 
 * specified, all {@link List}s that are returned are sorted by the {@link Task}
 * value, in descending order (highest value first).
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TaskService 
	extends UpdateService {
	
	//TODO set active task etc?
	
	/**
	 * Add a task
	 * 
	 * @param task the task to add
	 */
	public void addTask(Task task);
	
	/**
	 * Add tasks
	 * 
	 * @param tasks the tasks to add
	 */
	public void addTasks(Iterable<Task> tasks);
	
	/**
	 * Remove a task
	 * 
	 * @param task the task to remove
	 */
	public void removeTask(Task task);
	
	/**
	 * Remove all tasks of the given type
	 * 
	 * @param type the type name
	 */
	public void removeTasks(String type);
	
	/**
	 * Set the user data for the given task
	 * 
	 * @param task the task
	 * @param userData the task user data
	 */
	public void setUserData(Task task, TaskUserData userData);
	
	/**
	 * Get the tasks
	 * 
	 * @return a collection of all tasks
	 */
	public Collection<Task> getTasks();
	
	/**
	 * Resolves the tasks and returns them
	 * 
	 * @return the resolved tasks
	 */
	public Collection<ResolvedTask> getResolvedTasks();

	/**
	 * Resolves the given task
	 * 
	 * @param task the task to resolve
	 * 
	 * @return the resolved task
	 */
	public ResolvedTask resolveTask(Task task);
	
	// task provider managment
	
	/**
	 * Activate the task provider with the given ID
	 * 
	 * @param id the task provider id 
	 */
	public void activateTaskProvider(String id);
	
	/**
	 * Deactivate the task provider with the given ID
	 * 
	 * @param id the task provider id
	 */
	public void deactivateTaskProvider(String id);
	
	/**
	 * Determine if the task provider with the given ID is active
	 * 
	 * @param id the task provider id
	 * 
	 * @return if the task provider is active
	 */
	public boolean taskProviderIsActive(String id);

	/**
	 * Get the user tasks
	 * 
	 * @return the user tasks
	 */
	public Map<Task, TaskUserData> getUserTasks();
	
	/**
	 * Clear the user data associated with the tasks
	 */
	public void clearUserTasks();

}
