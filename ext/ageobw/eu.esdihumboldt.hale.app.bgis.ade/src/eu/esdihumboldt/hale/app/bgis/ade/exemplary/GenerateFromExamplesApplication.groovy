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

package eu.esdihumboldt.hale.app.bgis.ade.exemplary;

import org.eclipse.equinox.app.IApplicationContext

import eu.esdihumboldt.hale.common.app.AbstractApplication

/**
 * Application that generates an extended mapping from a mapping project with
 * example mappings.
 * 
 * @author Simon Templer
 */
class GenerateFromExamplesApplication extends AbstractApplication<GenerateFromExamplesContext> {

	@Override
	protected Object run(GenerateFromExamplesContext executionContext, IApplicationContext appContext) {
		new GenerateFromExamples().generate(executionContext)

		EXIT_OK
	}

	@Override
	protected void processParameter(String param, String value,
	GenerateFromExamplesContext executionContext) throws Exception {
		switch (param) {
			case '-project':
				executionContext.project = URI.create(value)
				break;
			case '-out':
				executionContext.out = new File(value)
				break;
		}
	}

	@Override
	protected GenerateFromExamplesContext createExecutionContext() {
		new GenerateFromExamplesContext()
	}
}
