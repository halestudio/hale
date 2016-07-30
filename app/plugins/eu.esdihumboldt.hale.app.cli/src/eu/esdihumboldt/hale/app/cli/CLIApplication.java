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

package eu.esdihumboldt.hale.app.cli;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import eu.esdihumboldt.util.cli.Runner;

/**
 * CLI application.
 * 
 * @author Simon Templer
 */
public class CLIApplication implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		String[] args = (String[]) context.getArguments().get("application.args");

		Runner runner = new Runner("HALE -nosplash -application hale.cli");

		try {
			return runner.run(args);
		} catch (Throwable e) {
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	public void stop() {
		// nothing to do
	}

}
