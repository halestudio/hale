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

package eu.esdihumboldt.util.cli.impl

import eu.esdihumboldt.util.cli.CommandContext
import groovy.transform.CompileStatic

/**
 * Context for delegating commands.
 * 
 * @author Simon Templer
 */
@CompileStatic
class DelegatingContext implements CommandContext {

	private final CommandContext delegate
	private final String subCommand

	public DelegatingContext(CommandContext delegate, String subCommand) {
		super()
		this.delegate = delegate
		this.subCommand = subCommand
	}

	@Override
	public String getBaseCommand() {
		delegate.baseCommand + ' ' + subCommand
	}

	@Override
	public String getCommandName() {
		subCommand
	}
}
