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

package eu.esdihumboldt.cst.functions.groovy.helper;

/**
 * Interface for helper functions that can be accessed from Groovy scripts.
 * 
 * @author Simon Templer
 * @author Sameer Sheikh
 * @param <R> the function return type
 */
public interface HelperFunction<R> {

	/**
	 * Call the function.
	 * 
	 * @param arg the single function argument or a Map of parameter names to
	 *            parameter values (to be called as named parameters)
	 * @return the function result
	 * @throws Exception if the function call fails due to an exception
	 */
	public R call(Object arg) throws Exception;

	/**
	 * get the specification of a function
	 * 
	 * @param functionName the name the function is registered with
	 * @return The specification of the function
	 * @throws Exception if function call fails
	 */
	public HelperFunctionSpecification getSpec(String functionName) throws Exception;

}
