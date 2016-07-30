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

package eu.esdihumboldt.hale.common.cli.impl

import eu.esdihumboldt.hale.common.cli.CLIUtil
import eu.esdihumboldt.hale.common.cli.Command
import eu.esdihumboldt.hale.common.cli.CommandContext
import eu.esdihumboldt.hale.common.cli.bash.BashCompletion
import groovy.transform.CompileStatic

/**
 * Command that delegates to other commands.
 * 
 * @author Simon Templer
 */
@CompileStatic
abstract class DelegatingCommand implements Command {

	abstract Map<String, Command> getSubCommands()

	@Override
	public int run(List<String> args, CommandContext context) {
		if (args.size() == 0) {
			// usage - list sub-commands
			CLIUtil.printUsage(context, subCommands)
			1
		}
		else {
			// select sub-command

			Command command
			def commandName
			if (args) {
				commandName = args[0]

				if (commandName == 'help' || commandName == '--help') {
					CLIUtil.printUsage(context, subCommands)
					return 0
				}

				command = subCommands[commandName]
			}

			if (command) {
				if (args.size() > 1) {
					args = args[1..-1]
				}
				else {
					args = []
				}

				// run a command
				def subContext = new DelegatingContext(context, commandName)
				command.run(args, subContext)
			}
			else {
				CLIUtil.printUsage(context, subCommands)
				1
			}
		}
	}

	@Override
	BashCompletion bashCompletion(List<String> args, int current) {
		if (current > 0) {
			// delegate to command
			String commandName = args[0]
			Command command = subCommands[commandName]
			if (command) {
				command.bashCompletion(args[1..-1], current - 1)
			}
			else {
				null
			}
		}
		else {
			// complete subcommand
			BashCompletion.command('compgen -W "help ' + subCommands.keySet().join(' ') + '" -- ' + args[current])
		}
	}

}
