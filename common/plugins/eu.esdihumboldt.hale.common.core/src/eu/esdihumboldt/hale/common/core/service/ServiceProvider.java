/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.service;

/**
 * Interface for retrieving a service in a given context.
 * 
 * @author Simon Templer
 */
public interface ServiceProvider {

	/**
	 * Get the service of the given type if available.
	 * 
	 * @param serviceInterface the service interface or type
	 * @return the service instance in the current context or <code>null</code>
	 *         if no such service exists
	 */
	public <T> T getService(Class<T> serviceInterface);

}
