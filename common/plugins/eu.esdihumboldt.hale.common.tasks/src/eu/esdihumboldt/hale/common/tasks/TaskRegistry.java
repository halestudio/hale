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

/**
 * Task type registry interface
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TaskRegistry {

	/**
	 * Register a task type
	 * 
	 * @param type the task type
	 * 
	 * @throws IllegalStateException if a type with the same type name already
	 *             exists
	 */
	void registerType(TaskType<?> type) throws IllegalStateException;

	/**
	 * Get the task type with the given name
	 * 
	 * @param typeName the task type name
	 * 
	 * @return the task type or <code>null</code> if no type with the given name
	 *         is registered
	 */
	TaskType<?> getType(String typeName);
}
