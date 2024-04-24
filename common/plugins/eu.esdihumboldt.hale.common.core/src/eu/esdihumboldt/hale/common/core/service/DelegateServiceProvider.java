/*
 * Copyright (c) 2024 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.core.service;

/**
 * A DelegateServiceProvider delegates service requests to an underlying
 * ServiceProvider. It implements the ServiceProvider interface and forwards
 * getService calls to the specified underlying ServiceProvider.
 * 
 * @author EmanuelaEpure
 */
public class DelegateServiceProvider implements ServiceProvider {

	private final ServiceProvider serviceProvider;

	/**
	 * Constructs a new DelegateServiceProvider with the specified
	 * ServiceProvider.
	 * 
	 * @param serviceProvider the underlying ServiceProvider to delegate to
	 */
	public DelegateServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Override
	public <T> T getService(Class<T> serviceInterface) {
		return serviceProvider.getService(serviceInterface);
	}

}
