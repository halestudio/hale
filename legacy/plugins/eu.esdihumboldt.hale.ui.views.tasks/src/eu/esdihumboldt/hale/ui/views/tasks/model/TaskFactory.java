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
 * Factory for tasks of a certain type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskFactory {
	
	/**
	 * Sets a type name prefix, prevents conflicts in case this type/factory is
	 *   used in multiple task providers
	 *   
	 * @param prefix the type name prefix
	 */
	public void setTypeNamePrefix(String prefix);
	
	/**
	 * Get the name of the task type
	 * 
	 * @return the name of the task type
	 */
	public String getTaskTypeName();
	
	/**
	 * Get the task type
	 * 
	 * @return the task type
	 */
	public TaskType getTaskType();
	
	/**
	 * Create a task for the given definitions. The task factory must check if
	 * input is valid before creating a task
	 * 
	 * @param serviceProvider the service provider
	 * @param definitions the definitions
	 * 
	 * @return the created task or <code>null</code> if no task for the given
	 *   definitions was created
	 */
	public Task createTask(ServiceProvider serviceProvider, Definition... definitions);

}
