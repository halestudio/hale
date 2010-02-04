/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.models.task;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.task.Task;

/**
 * Dedicated listener for {@link TaskService}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface TaskServiceListener extends HaleServiceListener {
	
	/**
	 * Called when tasks have been added
	 * 
	 * @param tasks the tasks that have been added
	 */
	public void tasksAdded(Iterable<Task> tasks);
	
	/**
	 * Called when a task has been removed
	 * 
	 * @param task the task that has been removed
	 */
	public void taskRemoved(Task task);

}
