/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.core.io;

import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Interface for discovering I/O advisors.
 * 
 * @author Simon Templer
 */
public interface IOAdvisorRegister {

	/**
	 * Find the advisor for an action
	 * 
	 * @param actionId the action identifier
	 * @param serviceProvider the service provider the new advisor shall be
	 *            configured with
	 * @return the advisor or <code>null</code>
	 */
	IOAdvisor<?> findAdvisor(final String actionId, final ServiceProvider serviceProvider);

}
