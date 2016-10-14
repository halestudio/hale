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

		boolean hasExperimental = commands.values().any { it.experimental }

		if (maxEntry) {
			print '  help'
			int pad = hasExperimental ? (maxEntry.length() + 3) : (maxEntry.length() - 1)
			print(' - '.padLeft(pad))
			print 'Show this help'
			println()

			commands.sort().each { name, command ->
				print "  $name"
				if (hasExperimental) {
					if (command.experimental) {
						print ' (*)'.padLeft(maxEntry.length() - name.length() + 4)
					}
					else {
						print '    '
					}
				}
				if (command.shortDescription) {
					if (command.experimental) {
						print ' - '
					}
					else {
						print(' - '.padLeft(maxEntry.length() - name.length() + 3))
					}
					print command.shortDescription
				}
				println()
			}

			if (hasExperimental) {
				println()
				println '(*) Command is experimental and the usage may be subject to breaking changes'
			}
		}
	}
}
