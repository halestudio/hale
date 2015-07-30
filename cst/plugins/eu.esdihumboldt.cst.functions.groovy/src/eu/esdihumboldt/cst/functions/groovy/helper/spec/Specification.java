/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.helper.spec;

import java.util.List;

/**
 * Helper function specification.
 * 
 * @author Simon Templer
 */
public interface Specification {

	/**
	 * Provides the description about what the function does.
	 * 
	 * @return the function description
	 */
	public String getDescription();

	/**
	 * Provides the list of arguments that the function takes.
	 * 
	 * @return arguments argument list of a function
	 */
	public List<? extends Argument> getArguments();

	/**
	 * get the description about the result of a function call.
	 * 
	 * @return result description of a function
	 */
	public String getResultDescription();
}
