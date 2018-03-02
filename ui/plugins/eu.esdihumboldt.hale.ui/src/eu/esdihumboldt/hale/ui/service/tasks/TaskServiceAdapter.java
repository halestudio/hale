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

package eu.esdihumboldt.hale.ui.service.tasks;

import eu.esdihumboldt.hale.common.tasks.ResolvedTask;
import eu.esdihumboldt.hale.common.tasks.Task;
import eu.esdihumboldt.hale.common.tasks.TaskServiceListener;

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
	public void tasksRemoved(Iterable<Task<?>> tasks) {
		// override me
	}

	/**
	 * @see TaskServiceListener#tasksAdded(Iterable)
	 */
	@Override
	public <C> void tasksAdded(Iterable<Task<C>> tasks) {
		// override me
	}

	/**
	 * @see TaskServiceListener#taskUserDataChanged(ResolvedTask)
	 */
	@Override
	public void taskUserDataChanged(ResolvedTask<?> task) {
		// override me
	}
}
