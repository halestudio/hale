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

package eu.esdihumboldt.hale.ui.views.tasks.model.impl;

import java.util.List;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.Task;

/**
 * Default task implementation
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefaultTask extends BaseTask implements Task, Comparable<Task> {
	
	/**
	 * The service provider that may be used to retrieve services
	 */
	protected final ServiceProvider serviceProvider;
	
	/**
	 * Create a new task
	 * 
	 * @param serviceProvider the service provider
	 * @param typeName the name of the task type
	 * @param context the task context
	 */
	public DefaultTask(ServiceProvider serviceProvider, String typeName, 
			List<? extends Definition> context) {
		super(typeName, context);
		this.serviceProvider = serviceProvider;
	}

}
