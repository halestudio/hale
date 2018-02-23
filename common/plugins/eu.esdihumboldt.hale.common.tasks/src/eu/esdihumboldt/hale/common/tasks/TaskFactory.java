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

package eu.esdihumboldt.hale.common.tasks;

import java.util.Collection;

/**
 * Factory for tasks of a certain type
 *
 * @param <C> Type of the context objects
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskFactory<C> {

	/**
	 * Get the task type
	 * 
	 * @return the task type
	 */
	public TaskType<C> getTaskType();

	/**
	 * Create a task for the given definitions. The task factory must check if
	 * input is valid before creating a task
	 * 
	 * @param context Context object
	 * 
	 * @return the created task or <code>null</code> if no task for the given
	 *         definitions was created
	 */
	Collection<Task<C>> createTasks(C context);

}
