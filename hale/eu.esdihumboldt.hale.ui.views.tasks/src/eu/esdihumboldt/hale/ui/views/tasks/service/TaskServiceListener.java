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

package eu.esdihumboldt.hale.ui.views.tasks.service;

import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;

/**
 * Dedicated listener for {@link TaskService}s
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskServiceListener extends HaleServiceListener {
	
	/**
	 * Called when tasks have been added
	 * 
	 * @param tasks the tasks that have been added
	 */
	public void tasksAdded(Iterable<Task> tasks);
	
	/**
	 * Called when tasks have been removed
	 * 
	 * @param tasks the tasks that have been removed
	 */
	public void tasksRemoved(Iterable<Task> tasks);
	
	/**
	 * Called when the user data of a task has changed
	 * 
	 * @param task the resolved task
	 */
	public void taskUserDataChanged(ResolvedTask task);

}
