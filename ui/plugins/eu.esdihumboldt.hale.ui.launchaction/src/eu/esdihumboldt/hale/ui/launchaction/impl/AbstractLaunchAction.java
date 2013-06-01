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

package eu.esdihumboldt.hale.ui.launchaction.impl;

import org.eclipse.equinox.app.IApplicationContext;

import eu.esdihumboldt.hale.ui.launchaction.LaunchAction;

/**
 * Base implementation of a launch action.
 * 
 * @author Simon Templer
 * @param <C> the action context type
 */
public abstract class AbstractLaunchAction<C> implements LaunchAction {

	private C launchContext;

	/**
	 * Create the launch context.
	 * 
	 * @return the launch context
	 */
	protected abstract C createLaunchContext();

	/**
	 * Process the command line arguments.
	 * 
	 * @param args the command line arguments
	 * @param launchContext the launch context to configure
	 */
	protected void processCommandLineArguments(String[] args, C launchContext) {
		if (args == null)
			return;
		for (int i = 0; i < args.length; i++) {
			// check for args without parameters (i.e., a flag arg)
			processFlag(args[i], launchContext);

			// check for args with parameters. If we are at the last argument or
			// if the next one
			// has a '-' as the first character, then we can't have an arg with
			// a param so continue.
			if (i == args.length - 1 || args[i + 1].startsWith("-")) //$NON-NLS-1$
				continue;
			processParameter(args[i], args[++i], launchContext);
		}
	}

	/**
	 * Process a single command line argument.
	 * 
	 * @param arg the argument
	 * @param executionContext the execution context to configure
	 */
	protected void processFlag(String arg, C executionContext) {
		// override me
	}

	/**
	 * Process a command line parameter
	 * 
	 * @param param the parameter name
	 * @param value the parameter value
	 * @param launchContext the launch context to configure
	 */
	protected void processParameter(String param, String value, C launchContext) {
		// override me
	}

	@Override
	public void init(IApplicationContext context) {
		launchContext = createLaunchContext();
		processCommandLineArguments((String[]) context.getArguments().get("application.args"),
				launchContext);
	}

//	/**
//	 * Dispose the launch context.
//	 * 
//	 * @param launchContext the launch context
//	 */
//	protected void dispose(C launchContext) {
//		// do nothing by default
//	}

}
