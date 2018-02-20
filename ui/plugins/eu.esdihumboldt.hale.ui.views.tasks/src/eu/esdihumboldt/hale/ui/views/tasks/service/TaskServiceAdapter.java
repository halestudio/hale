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
