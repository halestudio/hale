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

package eu.esdihumboldt.hale.io.validation.ui;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.io.validation.ui.service.ValidationRulesService;
import eu.esdihumboldt.hale.io.validation.ui.service.internal.ValidationRulesServiceImpl;

/**
 * Service factory for {@link ValidationRulesService}.
 * 
 * @author Florian Esser
 */
public class ServiceFactory extends AbstractServiceFactory {

	/**
	 * @see org.eclipse.ui.services.AbstractServiceFactory#create(java.lang.Class,
	 *      org.eclipse.ui.services.IServiceLocator,
	 *      org.eclipse.ui.services.IServiceLocator)
	 */
	@Override
	public Object create(Class serviceInterface, IServiceLocator parentLocator,
			IServiceLocator locator) {

		if (serviceInterface.equals(ValidationRulesService.class)) {
			return new ValidationRulesServiceImpl();
		}

		return null;
	}

}
