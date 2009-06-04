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
package eu.esdihumboldt.hale.models;

import java.util.List;
import java.util.Set;

import eu.esdihumboldt.hale.task.Task;

/**
 * The {@link TaskService} provides information on all Tasks that have been 
 * created for the current Alignment project. It stores tasks in all kinds of 
 * statii, including completed and obsolete ones.
 * 
 * Note that if during construction of this service no Comparator&lt;Task&gt; is 
 * specified, all {@link List}s that are returned are sorted by the {@link Task}
 * value, in descending order (highest value first).
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TaskService 
	extends UpdateService {
	
	public Task activateTask(Task task);

	/**
	 * @return the active {@link Task}, or null if there is no active task.
	 */
	public Task getActiveTask();
	
	/**
	 * @return a {@link List} of all {@link Task}s with status NEW or ACTIVE.
	 * This List is always ordered by using the Comparator specified at 
	 * construction time of this {@link TaskService}.
	 */
	public Set<Task> getOpenTasks();
	
	/**
	 * Add a single {@link Task} to the {@link TaskService}.
	 * @param task the {@link Task} to add.
	 * @return true if the adding was successful.
	 */
	public boolean addTask(Task task);
	
	/**
	 * Add a {@link List} of {@link Task}s to this {@link TaskService}.
	 * @param tasks the {@link List} of {@link Task}s to add.
	 * @return true if the adding was successful.
	 */
	public boolean addTasks(Set<Task> tasks);
	
}
