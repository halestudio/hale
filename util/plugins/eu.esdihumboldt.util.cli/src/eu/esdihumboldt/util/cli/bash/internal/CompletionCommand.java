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

package eu.esdihumboldt.util.cli.bash.internal;

import eu.esdihumboldt.util.cli.bash.BashCompletion;

/**
 * Bash completion represented by a Unix command.
 * 
 * @author Simon Templer
 */
public class CompletionCommand implements BashCompletion {

	private final String command;

	@SuppressWarnings("javadoc")
	public CompletionCommand(String command) {
		super();
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}

}
