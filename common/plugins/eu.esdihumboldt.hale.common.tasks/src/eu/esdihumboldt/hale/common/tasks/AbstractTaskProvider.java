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

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract task provider
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractTaskProvider implements TaskProvider {

	private boolean active = false;

	/**
	 * The task service
	 */
	protected TaskService taskService;

	private final Map<String, TaskFactory<?>> factories = new HashMap<>();

	/**
	 * Add a task factory. It must be added before
	 * {@link #registerTaskTypes(TaskRegistry)} is called (e.g in the
	 * constructor)
	 * 
	 * @param factory the factory to add
	 */
	protected void addFactory(TaskFactory<?> factory) {
		factories.put(factory.getTaskType().getName(), factory);
	}

	/**
	 * Get the factory for the given type name
	 * 
	 * @param typeName the type name
	 * 
	 * @return the task factory or <code>null</code>
	 */
	protected TaskFactory<?> getFactory(String typeName) {
		return factories.get(typeName);
	}

	/**
	 * @see TaskProvider#registerTaskTypes(TaskRegistry)
	 */
	@Override
	public void registerTaskTypes(TaskRegistry taskRegistry) {
		for (TaskFactory<?> factory : factories.values()) {
			taskRegistry.registerType(factory.getTaskType());
		}
	}

	@Override
	public void activate(TaskService taskService) {
		this.taskService = taskService;

		if (!active) {
			active = true;
			doActivate(taskService);
		}
	}

	/**
	 * Initialize the task provider. This method is called after registering the
	 * task types
	 * 
	 * @param taskService the TaskService instance
	 */
	protected abstract void doActivate(TaskService taskService);

}
