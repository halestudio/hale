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
