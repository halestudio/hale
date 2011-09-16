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

import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Task provider interface
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskProvider {
	
	/**
	 * Register the task types provided by this task provider. This method must
	 *   be called before calling {@link #activate(TaskService, ServiceProvider)}
	 *  
	 * @param taskRegistry the task type registry to register the types at
	 */
	public void registerTaskTypes(TaskRegistry taskRegistry);
	
	/**
	 * Initialize the task provider. This method is called after registering
	 *   the task types
	 * 
	 * @param taskService the task service
	 * @param serviceProvider the service provider
	 */
	public void activate(TaskService taskService, ServiceProvider serviceProvider);
	
	/**
	 * Clean up the task provider. This method is called when the task provider
	 *   is deactivated
	 */
	public void deactivate();
	
	/**
	 * Determine if the task provider is currently active
	 * 
	 * @return if the task provider is active
	 */
	public boolean isActive();

}
