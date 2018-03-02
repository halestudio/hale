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
package eu.esdihumboldt.hale.ui.index;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.index.internal.InstanceIndexUpdateServiceImpl;

/**
 * Service factory for the instance index update service.
 * 
 * @author Florian Esser
 */
public class ServiceFactory extends AbstractServiceFactory {

	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface,
			IServiceLocator parentLocator, IServiceLocator locator) {
		if (InstanceIndexUpdateService.class.equals(serviceInterface)) {
			return new InstanceIndexUpdateServiceImpl(HaleUI.getServiceProvider());
		}

		return null;
	}

}
