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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;
import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskRegistry;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskUserData;
import eu.esdihumboldt.hale.ui.views.tasks.model.extension.TaskProviderExtension;
import eu.esdihumboldt.hale.ui.views.tasks.model.extension.TaskProviderFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.impl.EclipseServiceProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.preferences.TaskPreferenceUtils;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * This is the standard implementation of the {@link TaskService}.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskServiceImpl extends AbstractTaskService {
	
	private static ALogger _log = ALoggerFactory.getLogger(TaskServiceImpl.class);
	
	private static volatile TaskService instance;
	
	private final TaskRegistry registry = new TaskRegistryImpl();
	
	private final ServiceProvider serviceProvider = new EclipseServiceProvider();
	
	private final SortedSet<Task> tasks = new TreeSet<Task>();
	
	private final Map<Task, TaskUserData> taskUserData = new HashMap<Task, TaskUserData>();
	
	/**
	 * The task provider instances
	 */
	private final Map<String, TaskProvider> taskProviders = new HashMap<String, TaskProvider>();
	
	// Constructor/ instance access ............................................
	
	/**
	 * Default constructor
	 */
	public TaskServiceImpl() {
		super();
		
		List<TaskProviderFactory> factories = TaskProviderExtension.getTaskProviderFactories();
		for (TaskProviderFactory factory : factories) {
			TaskProvider taskProvider = factory.getTaskProvider();
			if (taskProvider != null) {
				// register task types
				taskProvider.registerTaskTypes(registry);
				
				// activate provider
				if (TaskPreferenceUtils.getTaskProviderActive(factory.getId())) {
					taskProvider.activate(this, serviceProvider);
				}
				
				taskProviders.put(factory.getId(), taskProvider);
			}
		}
	}
	
	/**
	 * Get the task service instance
	 * 
	 * @return the task service instance
	 */
	public static TaskService getInstance() {
		if (instance == null) {
			instance = new TaskServiceImpl();
		}
		
		return instance;
	}
	
	// TaskService methods .....................................................

	/**
	 * @see TaskService#addTask(Task)
	 */
	@Override
	public void addTask(Task task) {
		if (addTaskInternal(task)) {
			notifyTasksAdded(Collections.singleton(task));
		}
	}

	/**
	 * Add a task without notifying the listeners
	 * 
	 * @param task the task to add
	 * @return if the task was added (it was no duplicate)
	 */
	private boolean addTaskInternal(Task task) {
		synchronized (tasks) {
			if (tasks.contains(task)) {
				// task is a duplicate
				task.dispose();
				return false;
			}
			tasks.add(task);
		}
		task.setTaskService(this);
		return true;
	}

	/**
	 * @see TaskService#addTasks(Iterable)
	 */
	@Override
	public void addTasks(Iterable<Task> tasks) {
		Collection<Task> added = new ArrayList<Task>();
		for (Task task : tasks) {
			if (addTaskInternal(task)) {
				added.add(task);
			}
		}
		
		notifyTasksAdded(added);
	}

	/**
	 * @see TaskService#getResolvedTasks()
	 */
	@Override
	public Collection<ResolvedTask> getResolvedTasks() {
		List<ResolvedTask> result = new ArrayList<ResolvedTask>();
		synchronized (tasks) {
			for (Task task : tasks) {
				ResolvedTask resolved = resolveTask(task);
				if (resolved != null) {
					result.add(resolved);
				}
				else {
					_log.error("Could not resolve task with type " + task.getTypeName()); //$NON-NLS-1$
				}
			}
		}
		return result;
	}
	
	/**
	 * @see TaskService#getResolvedTasks()
	 */
	@Override
	public Collection<Task> getTasks() {
		List<Task> result;
		synchronized (tasks) {
			result = new ArrayList<Task>(tasks);
		}
		return result;
	}

	/**
	 * @see TaskService#removeTask(Task)
	 */
	@Override
	public void removeTask(Task task) {
		boolean removed;
		synchronized (tasks) {
			removed = tasks.remove(task);
		}
		
		if (removed) {
			task.dispose();
			notifyTasksRemoved(Collections.singleton(task));
		}
	}

	/**
	 * @see TaskService#removeTasks(String)
	 */
	@Override
	public void removeTasks(String type) {
		Collection<Task> toRemove = new HashSet<Task>();
		synchronized (tasks) {
			for (Task task : tasks) {
				if (task.getTypeName().equals(type)) {
					toRemove.add(task);
				}
			}
			
			tasks.removeAll(toRemove);
		}
		
		for (Task task : toRemove) {
			// dispose removed tasks
			task.dispose();
		}
		
		notifyTasksRemoved(toRemove);
	}

	/**
	 * @see TaskService#setUserData(Task, TaskUserData)
	 */
	@Override
	public void setUserData(Task task, TaskUserData userData) {
		if (userData == null) {
			this.taskUserData.remove(task);
		}
		else {
			this.taskUserData.put(task, userData);
		}
		
		if (tasks.contains(task)) {
			notifyTaskUserDataChanged(resolveTask(task));
		}
	}

	/**
	 * @see TaskService#resolveTask(Task)
	 */
	@Override
	public ResolvedTask resolveTask(Task task) {
		return ResolvedTask.resolveTask(registry, task, taskUserData.get(task));
	}

	/**
	 * @see TaskService#activateTaskProvider(String)
	 */
	@Override
	public void activateTaskProvider(String id) {
		TaskProvider taskProvider = taskProviders.get(id);
		
		if (taskProvider != null) {
			TaskPreferenceUtils.setTaskProviderActive(id, true);
			taskProvider.activate(this, serviceProvider);
		}
	}

	/**
	 * @see TaskService#deactivateTaskProvider(String)
	 */
	@Override
	public void deactivateTaskProvider(String id) {
		TaskProvider taskProvider = taskProviders.get(id);
		
		if (taskProvider != null) {
			TaskPreferenceUtils.setTaskProviderActive(id, false);
			taskProvider.deactivate();
		}
	}

	/**
	 * @see TaskService#taskProviderIsActive(String)
	 */
	@Override
	public boolean taskProviderIsActive(String id) {
		return TaskPreferenceUtils.getTaskProviderActive(id);
	}

	/**
	 * @see TaskService#getUserTasks()
	 */
	@Override
	public Map<Task, TaskUserData> getUserTasks() {
		return taskUserData;
	}

	/**
	 * @see TaskService#clearUserTasks()
	 */
	@Override
	public void clearUserTasks() {
		List<Task> userDataTasks = new ArrayList<Task>();
		for (Task task : taskUserData.keySet()) {
			if (tasks.contains(task)) {
				userDataTasks.add(task);
			}
		}
		
		taskUserData.clear();
		
		for (Task task : userDataTasks) {
			notifyTaskUserDataChanged(resolveTask(task));
		}
	}
	
}
