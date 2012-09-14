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
