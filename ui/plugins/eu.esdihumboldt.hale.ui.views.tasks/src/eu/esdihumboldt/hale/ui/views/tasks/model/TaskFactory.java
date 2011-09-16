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
