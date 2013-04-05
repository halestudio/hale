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

package eu.esdihumboldt.hale.common.app;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * Base class for command line based applications.
 * 
 * @author Simon Templer
 * @param <C> the execution context type
 */
public abstract class AbstractApplication<C> implements IApplication {

	private C executionContext;

	@Override
	public Object start(IApplicationContext context) throws Exception {
		return run((String[]) context.getArguments().get("application.args"), context); //$NON-NLS-1$
	}

	/**
	 * Run the application.
	 * 
	 * @param args the application arguments
	 * @param appContext the application context
	 * @return the return value of the application
	 * @throws Exception if an unrecoverable error occurs processing the
	 *             arguments or running the application
	 * 
	 * @see IApplication#start(IApplicationContext)
	 */
	public Object run(String args[], IApplicationContext appContext) throws Exception {
		try {
			executionContext = createExecutionContext();
			processCommandLineArguments(args, executionContext);
			return run(executionContext, appContext);
		} catch (Exception e) {
			if (e.getMessage() != null)
				System.err.println(e.getMessage());
			else
				e.printStackTrace(System.err);
			throw e;
		}
	}

	/**
	 * Run the application.
	 * 
	 * @param executionContext the execution context configured based on the
	 *            application arguments
	 * @param appContext the application context
	 * @return the return value of the application
	 * 
	 * @see IApplication#start(IApplicationContext)
	 */
	protected abstract Object run(C executionContext, IApplicationContext appContext);

	/**
	 * Process the command line arguments.
	 * 
	 * @param args the command line arguments
	 * @param executionContext the execution context to configure
	 * @throws Exception if an unrecoverable error occurs processing the command
	 *             line
	 */
	protected void processCommandLineArguments(String[] args, C executionContext) throws Exception {
		if (args == null)
			return;
		for (int i = 0; i < args.length; i++) {
			// check for args without parameters (i.e., a flag arg)
			processFlag(args[i], executionContext);

			// check for args with parameters. If we are at the last argument or
			// if the next one
			// has a '-' as the first character, then we can't have an arg with
			// a param so continue.
			if (i == args.length - 1 || args[i + 1].startsWith("-")) //$NON-NLS-1$
				continue;
			processParameter(args[i], args[++i], executionContext);
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
	 * @param executionContext the execution context to configure
	 * @throws Exception if an unrecoverable error occurs processing the
	 *             parameter
	 */
	protected void processParameter(String param, String value, C executionContext)
			throws Exception {
		// override me
	}

	/**
	 * Create the application execution context.
	 * 
	 * @return the execution context
	 */
	protected abstract C createExecutionContext();

	@Override
	public void stop() {
		if (executionContext != null) {
			dispose(executionContext);
		}
	}

	/**
	 * Dispose the application execution context.
	 * 
	 * @param executionContext the execution context
	 */
	protected void dispose(C executionContext) {
		// do nothing by default
	}

}
