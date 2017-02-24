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

package eu.esdihumboldt.hale.common.core.io.util;

/**
 * Replaces variable references in a string. Mainly intended to be able to
 * validate inputs that include variable references.
 * 
 * @author Simon Templer
 */
public interface VariableReplacer {

	/**
	 * Replace variable references in a String by variable values. If variables
	 * cannot be resolved the an exception is thrown.
	 * 
	 * @param input the input string
	 * @return the input string w/ variable references replaced by values, if
	 *         present
	 */
	default String replaceVariables(String input) {
		return replaceVariables(input, true);
	}

	/**
	 * Replace variable references in a String by variable values.
	 * 
	 * @param input the input string
	 * @param failUnresolved if the call should fail with an exception if the
	 *            variables cannot be resolved
	 * @return the input string w/ variable references replaced by values, if
	 *         present
	 */
	public String replaceVariables(String input, boolean failUnresolved);

}
