/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.models.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.task.ResolvedTask;
import eu.esdihumboldt.hale.task.ServiceProvider;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskProvider;
import eu.esdihumboldt.hale.task.TaskRegistry;
import eu.esdihumboldt.hale.task.impl.EclipseServiceProvider;

/**
 * This is the standard implementation of the {@link TaskService}.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskServiceImpl extends AbstractTaskService {
	
	private static Logger _log = Logger.getLogger(TaskServiceImpl.class);
	
	private static volatile TaskService instance;
	
	private final TaskRegistry registry = new TaskRegistryImpl();
	
	private final ServiceProvider serviceProvider = new EclipseServiceProvider();
	
	private final SortedSet<Task> tasks = new TreeSet<Task>();
	
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
				
				//XXX for now, activate every task provider
				taskProvider.activate(this, serviceProvider);
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
		addTaskInternal(task);
		
		notifyTasksAdded(Collections.singleton(task));
	}

	/**
	 * Add a task without notifying the listeners
	 * 
	 * @param task the task to add
	 */
	private void addTaskInternal(Task task) {
		synchronized (tasks) {
			tasks.add(task);
		}
		task.setTaskService(this);
	}

	/**
	 * @see TaskService#addTasks(Iterable)
	 */
	@Override
	public void addTasks(Iterable<Task> tasks) {
		for (Task task : tasks) {
			addTaskInternal(task);
		}
		
		notifyTasksAdded(tasks);
	}

	/**
	 * @see TaskService#getResolvedTasks()
	 */
	@Override
	public Collection<ResolvedTask> getResolvedTasks() {
		List<ResolvedTask> result = new ArrayList<ResolvedTask>();
		synchronized (tasks) {
			for (Task task : tasks) {
				ResolvedTask resolved = ResolvedTask.resolveTask(registry, task);
				if (resolved != null) {
					result.add(resolved);
				}
				else {
					_log.error("Could not resolve task with type " + task.getTypeName());
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
			notifyTaskRemoved(task);
		}
	}

	/**
	 * @see TaskService#resolveTask(Task)
	 */
	@Override
	public ResolvedTask resolveTask(Task task) {
		return ResolvedTask.resolveTask(registry, task);
	}
	
}
