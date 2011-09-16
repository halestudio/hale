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

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskFactory;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskProvider;
import eu.esdihumboldt.hale.ui.views.tasks.model.TaskRegistry;
import eu.esdihumboldt.hale.ui.views.tasks.service.TaskService;

/**
 * Abstract task provider
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTaskProvider implements TaskProvider {
	
	private boolean active = false;
	
	private final String typePrefix;
	
	/**
	 * The service provider, it is first set in {@link #activate(TaskService, ServiceProvider)}
	 */
	protected ServiceProvider serviceProvider;
	
	/**
	 * The task service
	 */
	protected TaskService taskService;
	
	private final Map<String, TaskFactory> factories = new HashMap<String, TaskFactory>();
	
	/**
	 * Create a new task provider
	 * 
	 * @param typePrefix the type name prefix
	 */
	public AbstractTaskProvider(String typePrefix) {
		super();
		this.typePrefix = typePrefix;
	}

	/**
	 * Add a task factory. It must be added before {@link #registerTaskTypes(TaskRegistry)}
	 * is called (e.g in the constructor)
	 * 
	 * @param factory the factory to add
	 */
	protected void addFactory(TaskFactory factory) {
		factory.setTypeNamePrefix(typePrefix);
		factories.put(factory.getTaskType().getName(), factory);
	}
	
	/**
	 * Get the factory for the given type name
	 * 
	 * @param typeName the type name
	 * 
	 * @return the task factory or <code>null</code>
	 */
	protected TaskFactory getFactory(String typeName) {
		return factories.get(typeName);
	}
	
	/**
	 * @see TaskProvider#registerTaskTypes(TaskRegistry)
	 */
	@Override
	public void registerTaskTypes(TaskRegistry taskRegistry) {
		for (TaskFactory factory : factories.values()) {
			taskRegistry.registerType(factory.getTaskType());
		}
	}

	/**
	 * @see TaskProvider#activate(TaskService, ServiceProvider)
	 */
	@Override
	public void activate(TaskService taskService,
			ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
		this.taskService = taskService;
		
		if (!active) {
			active = true;
			doActivate(taskService);
		}
	}

	/**
	 * Initialize the task provider. This method is called after registering
	 *   the task types
	 * 
	 * @param taskService the task service
	 */
	protected abstract void doActivate(TaskService taskService);

	/**
	 * @see TaskProvider#deactivate()
	 */
	@Override
	public void deactivate() {
		if (active) {
			active = false;
			doDeactivate();
			// remove tasks that were created by this task provider (by type)
			for (String type : factories.keySet()) {
				taskService.removeTasks(type);
			}
		}
	}
	
	/**
	 * Clean up the task provider. This method is called when the task provider
	 *   is deactivated
	 */
	protected abstract void doDeactivate();

	/**
	 * @see TaskProvider#isActive()
	 */
	@Override
	public boolean isActive() {
		return active;
	}

}
