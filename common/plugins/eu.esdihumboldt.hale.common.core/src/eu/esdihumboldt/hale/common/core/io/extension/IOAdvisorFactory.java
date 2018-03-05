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

package eu.esdihumboldt.hale.common.core.io.extension;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Factory for {@link IOAdvisor}s.
 * 
 * Use {@link #createAdvisor(String, ServiceProvider)} to create an
 * {@link IOAdvisor} instance, calling {@link #createExtensionObject()} is
 * disallowed.
 * 
 * @author Simon Templer
 */
public interface IOAdvisorFactory extends ExtensionObjectFactory<IOAdvisor<?>> {

	/**
	 * Get the identifier of the action the advisor is associated with
	 * 
	 * @return the associated action ID
	 */
	public String getActionID();

	/**
	 * Create an I/O advisor with the given service provider.
	 * 
	 * @param actionId the action identifier
	 * @param serviceProvider the service provider the advisor will use to
	 *            access services
	 * @return the I/O advisor
	 * @throws Exception if instantiating the I/O advisor fails
	 */
	public IOAdvisor<?> createAdvisor(String actionId, ServiceProvider serviceProvider)
			throws Exception;

}
