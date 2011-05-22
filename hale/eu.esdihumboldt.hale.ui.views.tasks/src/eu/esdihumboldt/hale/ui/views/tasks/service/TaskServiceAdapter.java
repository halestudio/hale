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
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.hale.ui.views.tasks.model.ResolvedTask;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;

/**
 * Task service listener adapter
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class TaskServiceAdapter implements TaskServiceListener {

	/**
	 * @see TaskServiceListener#tasksRemoved(Iterable)
	 */
	@Override
	public void tasksRemoved(Iterable<Task> tasks) {
		// override me
	}

	/**
	 * @see TaskServiceListener#tasksAdded(Iterable)
	 */
	@Override
	public void tasksAdded(Iterable<Task> tasks) {
		// override me
	}

	/**
	 * @see TaskServiceListener#taskUserDataChanged(ResolvedTask)
	 */
	@Override
	public void taskUserDataChanged(ResolvedTask task) {
		// override me
	}

	/**
	 * @see HaleServiceListener#update(UpdateMessage)
	 */
	@Override
	public void update(@SuppressWarnings("rawtypes") UpdateMessage message) {
		// override me if you are sure you need to be called on any event
	}

}
