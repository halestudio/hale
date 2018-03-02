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
package eu.esdihumboldt.hale.ui.service.tasks.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.tasks.ResolvedTask;
import eu.esdihumboldt.hale.common.tasks.Task;
import eu.esdihumboldt.hale.common.tasks.TaskProvider;
import eu.esdihumboldt.hale.common.tasks.TaskRegistry;
import eu.esdihumboldt.hale.common.tasks.TaskService;
import eu.esdihumboldt.hale.common.tasks.TaskUserData;
import eu.esdihumboldt.hale.common.tasks.TaskUserDataAware;
import eu.esdihumboldt.hale.common.tasks.extension.TaskProviderExtension;
import eu.esdihumboldt.hale.common.tasks.extension.TaskProviderFactory;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * This is the standard implementation of the {@link TaskService}.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class TaskServiceImpl extends AbstractTaskService {

	private static ALogger _log = ALoggerFactory.getLogger(TaskServiceImpl.class);

	private final TaskRegistry registry = new TaskRegistryImpl();

	private final SortedSet<Task<?>> tasks = new TreeSet<>();

	private final Map<Task<?>, TaskUserData> taskUserData = new HashMap<>();

	/**
	 * The task provider instances
	 */
	private final Map<String, TaskProvider> taskProviders = new HashMap<>();

	// Constructor/ instance access ............................................

	/**
	 * Default constructor
	 */
	public TaskServiceImpl() {
		super();

		List<TaskProviderFactory> factories = TaskProviderExtension.getTaskProviderFactories();
		for (TaskProviderFactory factory : factories) {
			try {
				TaskProvider taskProvider = factory.createExtensionObject();
				if (taskProvider != null) {
					// register task types
					taskProvider.registerTaskTypes(registry);
					taskProvider.activate(this);

					taskProviders.put(factory.getIdentifier(), taskProvider);
				}
			} catch (Exception e) {
				_log.error("Couldn't create task provider", e);
			}
		}
	}

	// TaskService methods .....................................................

	/**
	 * @see TaskService#addTask(Task)
	 */
	@Override
	public void addTask(Task<?> task) {
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
	private boolean addTaskInternal(Task<?> task) {
		synchronized (tasks) {
			if (tasks.contains(task)) {
				// task is a duplicate
				task.dispose();
				return false;
			}
			tasks.add(task);
		}
//		task.setTaskService(this);
		return true;
	}

	/**
	 * @see TaskService#addTasks(Iterable)
	 */
	@Override
	public <C> void addTasks(Iterable<Task<C>> tasks) {
		Collection<Task<C>> added = new ArrayList<>();
		for (Task<C> task : tasks) {
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
	public Collection<ResolvedTask<?>> getResolvedTasks() {
		List<ResolvedTask<?>> result = new ArrayList<>();
		synchronized (tasks) {
			for (Task<?> task : tasks) {
				ResolvedTask<?> resolved = resolveTask(task);
				if (resolved != null) {
					result.add(resolved);
				}
				else {
					_log.error("Could not resolve task with type " + task.getTaskType().getName()); //$NON-NLS-1$
				}
			}
		}
		return result;
	}

	/**
	 * @see TaskService#getResolvedTasks()
	 */
	@Override
	public Collection<Task<?>> getTasks() {
		List<Task<?>> result;
		synchronized (tasks) {
			result = new ArrayList<>(tasks);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> Collection<Task<C>> getTasks(C context) {
		List<Task<C>> result;
		synchronized (tasks) {
			result = tasks.stream().filter(t -> t.getMainContext().equals(context))
					.map(t -> (Task<C>) t).collect(Collectors.toList());
		}

		return result;
	}

	/**
	 * @see TaskService#removeTask(Task)
	 */
	@Override
	public void removeTask(Task<?> task) {
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
		Collection<Task<?>> toRemove = new HashSet<>();
		synchronized (tasks) {
			for (Task<?> task : tasks) {
				if (task.getTaskType().getName().equals(type)) {
					toRemove.add(task);
				}
			}

			tasks.removeAll(toRemove);
		}

		for (Task<?> task : toRemove) {
			// dispose removed tasks
			task.dispose();
		}

		notifyTasksRemoved(toRemove);
	}

	/**
	 * @see TaskService#setUserData(Task, TaskUserData)
	 */
	@Override
	public void setUserData(Task<?> task, TaskUserData userData) {
		if (userData == null) {
			this.taskUserData.remove(task);
		}
		else {
			this.taskUserData.put(task, userData);
		}

		if (task instanceof TaskUserDataAware && ((TaskUserDataAware) task).setUserData(userData)) {
			ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
			if (ps != null) {
				ps.setChanged();
			}
		}

		if (tasks.contains(task)) {
			notifyTaskUserDataChanged(resolveTask(task));
		}
	}

	/**
	 * @see TaskService#resolveTask(Task)
	 */
	@Override
	public <C> ResolvedTask<C> resolveTask(Task<C> task) {
		return ResolvedTask.resolveTask(registry, task, taskUserData.get(task));
	}

	@Override
	public void activateTaskProvider(String id) {
		TaskProvider taskProvider = taskProviders.get(id);

		if (taskProvider != null) {
			taskProvider.activate(this);
		}
	}

	/**
	 * @see TaskService#getUserTasks()
	 */
	@Override
	public Map<Task<?>, TaskUserData> getUserTasks() {
		return taskUserData;
	}

	/**
	 * @see TaskService#clearUserTasks()
	 */
	@Override
	public void clearUserTasks() {
		List<Task<?>> userDataTasks = new ArrayList<Task<?>>();
		for (Task<?> task : taskUserData.keySet()) {
			if (tasks.contains(task)) {
				userDataTasks.add(task);
			}
		}

		taskUserData.clear();

		for (Task<?> task : userDataTasks) {
			notifyTaskUserDataChanged(resolveTask(task));
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.tasks.TaskService#clearTasks()
	 */
	@Override
	public void clearTasks() {
		clearUserTasks();
		notifyTasksRemoved(tasks);
		tasks.clear();
	}

}
