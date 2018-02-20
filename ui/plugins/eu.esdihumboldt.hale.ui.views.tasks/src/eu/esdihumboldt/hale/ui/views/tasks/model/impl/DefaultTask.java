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
