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

package eu.esdihumboldt.hale.common.cli

import groovy.transform.CompileStatic

/**
 * CLI utilities.
 * 
 * @author Simon Templer
 */
class CLIUtil {

	@CompileStatic
	static URI fileOrUri(String value) {
		try {
			URI uri = URI.create(value)
			if (uri.scheme && uri.scheme.length() > 1) {
				// only accept as URI if a schema is present
				// and the scheme is more than just one character
				// which is likely a Windows drive letter
				return uri
			}
			else {
				return new File(value).toURI()
			}
		} catch (e) {
			return new File(value).toURI()
		}
	}

	@CompileStatic
	static void printUsage(CommandContext context, Map<String, Command> commands) {
		println "usage: ${context.baseCommand} <command> [<args>]"
		println()
		println 'Supported commands are:'

		String maxEntry = commands.keySet().max { it.length() }

		if (maxEntry) {
			print '  help'
			print(' - '.padLeft(maxEntry.length() - 1))
			print 'Show this help'
			println()

			commands.sort().each { name, command ->
				print "  $name"
				if (command.shortDescription) {
					print(' - '.padLeft(maxEntry.length() - name.length() + 3))
					print command.shortDescription
				}
				println()
			}
		}
	}
}
