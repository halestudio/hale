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

/**
 * Context of a command execution.
 * 
 * @author Simon Templer
 */
public interface CommandContext {

	/**
	 * Get the base command line call for use in the command usage. Includes the
	 * call to the executable up to selecting this command.
	 * 
	 * @return the base command line call
	 */
	String getBaseCommand();

	/**
	 * Get the command name.
	 * 
	 * @return the command name
	 */
	String getCommandName();

}
