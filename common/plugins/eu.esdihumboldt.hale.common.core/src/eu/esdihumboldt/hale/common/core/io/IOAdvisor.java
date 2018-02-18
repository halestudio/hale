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

package eu.esdihumboldt.hale.common.core.io;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Advises in the configuration of an {@link IOProvider} in a certain context
 * (e.g. the UI services) and integrates the execution results into this
 * context.
 * 
 * @param <T> the I/O provider type supported
 * 
 * @author Simon Templer
 */
public interface IOAdvisor<T extends IOProvider> {

	/**
	 * Get the identifier of the corresponding action.
	 * 
	 * @return the action ID
	 */
	public String getActionId();

	/**
	 * Set the identifier of the action the advisor is associated to.
	 * 
	 * @param actionId the action identifier
	 */
	public void setActionId(String actionId);

	/**
	 * Set the service provider through which the advisor can access services in
	 * the current context. This method must be called before
	 * {@link #prepareProvider(IOProvider)},
	 * {@link #updateConfiguration(IOProvider)} or
	 * {@link #handleResults(IOProvider)} is called
	 * 
	 * @param serviceProvider the service provider
	 */
	public void setServiceProvider(ServiceProvider serviceProvider);

	/**
	 * Prepare the I/O provider when it is created. This may be executed even if
	 * for the provider no execution takes place.<br>
	 * <br>
	 * This for instance allows configuration pages on IOWizards to base on this
	 * preparation.
	 * 
	 * @param provider the I/O provider
	 */
	public void prepareProvider(T provider);

	/**
	 * Update the provider configuration directly before the execution.
	 * 
	 * @param provider the I/O provider
	 */
	public void updateConfiguration(T provider);

	/**
	 * Process the results after the execution.
	 * 
	 * @param provider the I/O provider
	 */
	public void handleResults(T provider);

}
