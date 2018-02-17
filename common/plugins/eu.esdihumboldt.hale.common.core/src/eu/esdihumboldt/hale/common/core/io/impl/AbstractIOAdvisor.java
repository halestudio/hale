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

package eu.esdihumboldt.hale.common.core.io.impl;

import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Abstract {@link IOAdvisor} base implementation
 * 
 * @param <T> the I/O provider type
 * 
 * @author Simon Templer
 */
public abstract class AbstractIOAdvisor<T extends IOProvider>
		implements IOAdvisor<T>, ServiceProvider {

	private ServiceProvider serviceProvider;
	private String action;

	@Override
	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	@Override
	public <X> X getService(Class<X> serviceInterface) {
		return serviceProvider.getService(serviceInterface);
	}

	@Override
	public String getActionId() {
		return action;
	}

	@Override
	public void setActionId(String actionId) {
		this.action = actionId;
	}

	/**
	 * @see IOAdvisor#prepareProvider(IOProvider)
	 */
	@Override
	public void prepareProvider(T provider) {
		provider.setActionId(getActionId());

		// override me
	}

	/**
	 * @see IOAdvisor#updateConfiguration(IOProvider)
	 */
	@Override
	public void updateConfiguration(T provider) {
		provider.setServiceProvider(serviceProvider);

		// override me
	}

	/**
	 * @see IOAdvisor#handleResults(IOProvider)
	 */
	@Override
	public void handleResults(T provider) {
		// override me
	}

}
