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

package eu.esdihumboldt.hale.common.cli.bash;

import eu.esdihumboldt.hale.common.cli.bash.internal.CompletionCommand;

/**
 * Represents a reply from the program regarding bash completion.
 * 
 * @author Simon Templer
 */
public interface BashCompletion {

	/**
	 * Keyword for bash file completion.
	 */
	static final String COMPLETION_FILE = "FILE";

	/**
	 * Bash completion based on a Unix command.
	 * 
	 * @param command the Unix command to use to generate bash completions
	 * @return the bash completion representation
	 */
	static BashCompletion command(String command) {
		return new CompletionCommand(command);
	}

	/**
	 * Bash completion for local files.
	 * 
	 * @return the bash completion representation
	 */
	static BashCompletion file() {
		return new CompletionCommand(COMPLETION_FILE);
	}

	/**
	 * Return a Unix command to use to generate bash completions. Alternatively
	 * a reserved keyword can be used to use a pre-configured completion.
	 * 
	 * @return the Unix command or <code>null</code>
	 */
	String getCommand();

}
