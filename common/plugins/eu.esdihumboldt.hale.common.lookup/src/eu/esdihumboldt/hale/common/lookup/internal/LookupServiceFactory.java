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

package eu.esdihumboldt.hale.common.lookup.internal;

import eu.esdihumboldt.hale.common.core.service.ServiceFactory;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.lookup.LookupService;

/**
 * Lookup service factory.
 * 
 * @author Simon Templer
 */
public class LookupServiceFactory implements ServiceFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T createService(Class<T> serviceInterface, ServiceProvider serviceLocator) {
		if (serviceInterface.equals(LookupService.class)) {
			return (T) new LookupServiceImpl();
		}

		return null;
	}

}
