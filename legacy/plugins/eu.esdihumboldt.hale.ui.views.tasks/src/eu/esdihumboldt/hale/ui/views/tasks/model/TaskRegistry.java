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

package eu.esdihumboldt.hale.ui.views.tasks.model;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;

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
	 *   exists 
	 */
	public void registerType(TaskType type) throws IllegalStateException;
	
	/**
	 * Get the task type with the given name
	 * 
	 * @param typeName the task type name
	 * 
	 * @return the task type or <code>null</code> if no type with the given name
	 *   is registered
	 */
	public TaskType getType(String typeName);
	
	/**
	 * Create a task for the given definitions. The task factory must check if
	 * input is valid before creating a task
	 * 
	 * @param serviceProvider the service provider
	 * @param typeName the type name of the task
	 * @param definitions the definitions
	 * 
	 * @return the created task or <code>null</code> if no task for the given
	 *   definitions was created
	 */
	public Task createTask(ServiceProvider serviceProvider, String typeName, Definition... definitions);

}
