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

package eu.esdihumboldt.hale.app.transform

import eu.esdihumboldt.hale.common.app.ApplicationUtil
import eu.esdihumboldt.util.cli.Command
import eu.esdihumboldt.util.cli.CommandContext
import groovy.transform.CompileStatic

@CompileStatic
class TransformCommand implements Command {

	@Override
	int run(List<String> args, CommandContext context) {
		ExecApplication app = new ExecApplication() {
					protected String getBaseCommand() {
						context.baseCommand
					}
				}

		def result = ApplicationUtil.launchSyncApplication(app, args as List)

		// interpret result as return code
		int returnCode
		try {
			returnCode = result as int
		} catch (e) {
			returnCode = 0
		}
		returnCode
	}

	final String shortDescription = 'Run a transformation based on a hale project'

}
