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

package eu.esdihumboldt.hale.ui.views.tasks.model.impl;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.views.tasks.model.ServiceProvider;

/**
 * Service provider that retrieves services using {@link PlatformUI}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class EclipseServiceProvider implements ServiceProvider {

	/**
	 * @see ServiceProvider#getService(Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> serviceType) {
		return (T) PlatformUI.getWorkbench().getService(serviceType);
	}

}
