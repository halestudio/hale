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

package eu.esdihumboldt.hale.common.cli;

import java.util.List;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.cli.bash.BashCompletion;

/**
 * Interface for CLI commands.
 * 
 * @author Simon Templer
 */
public interface Command {

	/**
	 * Run the command.
	 * 
	 * @param args the list of arguments
	 * @param context the command context
	 * @return the exit code
	 */
	int run(List<String> args, CommandContext context);

	/**
	 * Get a short description describing the command.
	 * 
	 * @return a short command description
	 */
	@Nullable
	String getShortDescription();

	/**
	 * Return a Unix command to use to generate bash completions.
	 * 
	 * @param args the list of arguments
	 * @param current the index of the current argument to be completed
	 * @return the Unix command or <code>null</code>
	 */
	@Nullable
	default BashCompletion bashCompletion(List<String> args, int current) {
		return null;
	}

}
