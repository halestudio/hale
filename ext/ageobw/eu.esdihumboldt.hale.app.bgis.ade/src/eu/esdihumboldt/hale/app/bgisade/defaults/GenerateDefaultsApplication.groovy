/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgisade.defaults;

import org.eclipse.equinox.app.IApplicationContext

import eu.esdihumboldt.hale.common.app.AbstractApplication

/**
 * Application that generates a mapping of default values.
 * 
 * @author Simon Templer
 */
class GenerateDefaultsApplication extends AbstractApplication<GenerateDefaultsContext> {

	@Override
	protected Object run(GenerateDefaultsContext executionContext, IApplicationContext appContext) {
		// TODO
		println 'App run'

		EXIT_OK
	}

	@Override
	protected GenerateDefaultsContext createExecutionContext() {
		new GenerateDefaultsContext()
	}
}
