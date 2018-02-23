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

/**
 * Task provider interface
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface TaskProvider {

	/**
	 * Register the task types provided by this task provider. This method must
	 * be called before calling {@link #activate(TaskService, ServiceProvider)}
	 * 
	 * @param taskRegistry the task type registry to register the types at
	 */
	public void registerTaskTypes(TaskRegistry taskRegistry);

	/**
	 * Initialize the task provider. This method is called after registering the
	 * task types
	 * 
	 * @param taskService the task service
	 * @param serviceProvider the service provider
	 */
	public void activate(TaskService taskService);

	/**
	 * Clean up the task provider. This method is called when the task provider
	 * is deactivated
	 */
//	public void deactivate();

	/**
	 * Determine if the task provider is currently active
	 * 
	 * @return if the task provider is active
	 */
//	public boolean isActive();

}
