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

package eu.esdihumboldt.util.cli

import eu.esdihumboldt.util.cli.bash.BashCompletion
import eu.esdihumboldt.util.cli.extension.GroupCommand
import eu.esdihumboldt.util.cli.impl.ContextImpl
import groovy.transform.CompileStatic

/**
 * Command line runner.
 * 
 * @author Simon Templer
 */
@CompileStatic
class Runner extends GroupCommand {

	private final String baseCommand

	Runner(String baseCommand) {
		this.baseCommand = baseCommand
	}

	final String shortDescription = 'hale command line utility'

	int run(String[] args) {
		CommandContext context = new ContextImpl(baseCommand, null)

		if (args) {
			// support --version
			if (args[0] == '--version') {
				args[0] = 'version'
			}

			// helper for bash completion
			if (args[0] == '--complete') {
				// next arg must be index of word to complete
				int currentWord
				try {
					currentWord = args[1] as int
				} catch (e) {
					return 1
				}

				// determine list of words (strip "--complete <index>")
				def words = args.length > 2 ? args[2..-1] as List : []

				// if current word is not included, add empty string as argument
				if (words.size() == currentWord) {
					words << ''
				}

				// strip first word (which is the base command)
				words = words.size() > 1 ? words[1..-1] : []

				BashCompletion completion = bashCompletion((List<String>) words, currentWord - 1)
				if (completion) {
					println completion.command
					return 0
				}
				else {
					return 1
				}
			}
		}

		run(args as List, context)
	}

}
