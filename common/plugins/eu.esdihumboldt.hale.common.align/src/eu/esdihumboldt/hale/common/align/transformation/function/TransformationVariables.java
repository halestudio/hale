/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.transformation.function;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Transformation variables for use in transformation functions.
 * 
 * @author Simon Templer
 */
public interface TransformationVariables {

	/**
	 * Get the value for a given variable name and scope.
	 * 
	 * @param scope the variable scope
	 * @param name the variable name
	 * @return the variable value or {@link Value#NULL}
	 */
	public Value getVariable(TransformationVariableScope scope, String name);

	/**
	 * Replace variable references in a String by variable values.
	 * 
	 * @param input the input string
	 * @return the input string w/ variable references replaced by values, if
	 *         present
	 */
	public String replaceVariables(String input);

}
