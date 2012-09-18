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

package eu.esdihumboldt.hale.ui.common.definition.internal;

import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import eu.esdihumboldt.hale.ui.common.definition.DefinitionLabelFactory;

/**
 * Service factory for table view related services
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ServiceFactory extends AbstractServiceFactory {

	/**
	 * @see AbstractServiceFactory#create(Class, IServiceLocator,
	 *      IServiceLocator)
	 */
	@Override
	public Object create(@SuppressWarnings("rawtypes") Class serviceInterface,
			IServiceLocator parentLocator, IServiceLocator locator) {
		if (serviceInterface.equals(DefinitionLabelFactory.class)) {
			return new DefaultDefinitionLabelFactory();
		}

		return null;
	}

}
