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
package eu.esdihumboldt.hale.models.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.Task.TaskStatus;

/**
 * This is the standard implementation of the {@link TaskService}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TaskServiceImpl 
	implements TaskService {
	
	private static Logger _log = Logger.getLogger(TaskServiceImpl.class);
	
	private static TaskService instance = new TaskServiceImpl();
	
	private Set<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();
	
	private Task activeTask;
	
	private SortedSet<Task> tasks;
	
	// Constructor/ instance access ............................................
	
	private TaskServiceImpl() {
		super();
		this.activeTask = null;
		this.tasks = new TreeSet<Task>(new TaskValueComparator());
	}
	
	public static TaskService getInstance() {
		return TaskServiceImpl.instance;
	}
	
	// TaskService methods .....................................................

	/**
	 * @see eu.esdihumboldt.hale.models.TaskService#addTask(eu.esdihumboldt.hale.task.Task, eu.esdihumboldt.hale.models.TaskService.TaskStatus)
	 */
	public boolean addTask(Task task) {
		this.tasks.add(task);
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.TaskService#addTasks(java.util.List, eu.esdihumboldt.hale.models.TaskService.TaskStatus)
	 */
	public boolean addTasks(Set<Task> tasks) {
		for (Task t : tasks) {
			this.tasks.add(t);
		}
		this.updateListeners();
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.TaskService#getActiveTask()
	 */
	public Task getActiveTask() {
		return this.activeTask;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.TaskService#getOpenTasks()
	 */
	@Override
	public Set<Task> getOpenTasks() {
		Set<Task> result = new HashSet<Task>();
		for (Task t : this.tasks) {
			if (t.getTaskStatus().equals(TaskStatus.ACTIVE) || t.getTaskStatus().equals(TaskStatus.NEW)) {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * 
	 */
	public Task activateTask(Task task) {
		this.activeTask = task;
		return null;
	}
	
	// UpdateService operations ................................................

	/**
	 * @see eu.esdihumboldt.hale.models.UpdateService#addListener(eu.esdihumboldt.hale.models.HaleServiceListener)
	 */
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			_log.info("Updating a listener.");
			hsl.update(new UpdateMessage(TaskService.class, null));
		}
	}

}
