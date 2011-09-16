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

import java.util.Collection;

import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskRegistry;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Combines several task providers
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CombinedTaskProvider implements TaskProvider {
	
	private final Collection<TaskProvider> providers;

	/**
	 * Create a new task provider combining the given providers
	 * 
	 * @param providers the task providers
	 */
	public CombinedTaskProvider(Collection<TaskProvider> providers) {
		super();
		this.providers = providers;
	}

	/**
	 * @see TaskProvider#activate(TaskService, ServiceProvider)
	 */
	@Override
	public void activate(TaskService taskService,
			ServiceProvider serviceProvider) {
		for (TaskProvider p : providers) {
			p.activate(taskService, serviceProvider);
		}
	}

	/**
	 * @see TaskProvider#deactivate()
	 */
	@Override
	public void deactivate() {
		for (TaskProvider p : providers) {
			p.deactivate();
		}
	}

	/**
	 * @see TaskProvider#isActive()
	 */
	@Override
	public boolean isActive() {
		for (TaskProvider p : providers) {
			if (p.isActive()) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @see TaskProvider#registerTaskTypes(TaskRegistry)
	 */
	@Override
	public void registerTaskTypes(TaskRegistry taskRegistry) {
		for (TaskProvider p : providers) {
			p.registerTaskTypes(taskRegistry);
		}
	}

}
