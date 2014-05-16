/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.app.transform;

import java.net.URI;

import org.eclipse.equinox.app.IApplicationContext

import eu.esdihumboldt.hale.common.app.AbstractApplication
import groovy.transform.CompileStatic;

/**
 * Application that executes a transformation triggered by FME and based on a
 * project file.
 * 
 * @author Simon Templer
 */
@CompileStatic
class ExecApplication extends AbstractApplication<ExecContext> {

	@Override
	protected Object run(ExecContext executionContext, IApplicationContext appContext) {
		// set system err to system out, otherwise system err messages seem to get lost
		System.setErr(System.out);
		
		new ExecTransformation().run(executionContext)

		EXIT_OK
	}

	@Override
	protected void processParameter(String param, String value,
	ExecContext executionContext) throws Exception {
		switch (param) {
			case '-project':
				executionContext.project = URI.create(value)
				break
			case '-source':
				executionContext.source = URI.create(value)
				break
			case '-out':
				executionContext.out = new File(value)
				break
			case '-reportsOut':
				executionContext.reportsOut = new File(value)
				break
			case '-preset':
				executionContext.preset = value
				break
		}
	}

	@Override
	protected void processFlag(String arg, ExecContext executionContext) {
		switch (arg) {
			// any?
		}
	}

	@Override
	protected ExecContext createExecutionContext() {
		new ExecContext()
	}
}
