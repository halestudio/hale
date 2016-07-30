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

package eu.esdihumboldt.util.cli.extension.command;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractExtension;
import eu.esdihumboldt.util.cli.Command;

/**
 * Extension for CLI commands.
 * 
 * @author Simon Templer
 */
public class CommandExtension extends AbstractExtension<Command, CommandFactory> {

	private static volatile CommandExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the command extension
	 */
	public static CommandExtension getInstance() {
		if (instance == null) {
			instance = new CommandExtension();
		}

		return instance;
	}

	/**
	 * Extension point identifier.
	 */
	public static final String EXTENSION_ID = "eu.esdihumboldt.cli";

	/**
	 * Default constructor.
	 */
	protected CommandExtension() {
		super(EXTENSION_ID);
	}

	@Override
	protected CommandFactory createFactory(IConfigurationElement conf) throws Exception {
		if ("command".equals(conf.getName())) {
			return new ConfigurationCommandFactory(conf);
		}
		return null;
	}

}
