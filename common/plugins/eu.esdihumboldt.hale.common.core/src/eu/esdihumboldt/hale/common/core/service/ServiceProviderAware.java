/*
 * Copyright (c) 2017 wetransform GmbH
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
 * Interface for classes aware of {@link ServiceProvider}, e.g. InstanceHandlers
 * requiring access to project-scoped services.
 * 
 * @author Florian Esser
 */
public interface ServiceProviderAware {

	/**
	 * Set the context service provider.
	 * 
	 * @param services the service provider
	 */
	void setServiceProvider(ServiceProvider services);
}
