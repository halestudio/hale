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

package eu.esdihumboldt.cst.functions.groovy.helper



/**
 * It denotes the specification details for helper functions.
 * 
 * @author sameer sheikh
 */
public class HelperFunctionSpecification {

	private final String description;
	private final List<HelperFunctionArgument> arguments;
	private final String resultDescription;

	/**
	 * parameterized constructor
	 *  
	 * @param resultDescription description about the return of the function 
	 * @param description Description about a function
	 * @param arguments arguments that can be passed into the function
	 */
	public HelperFunctionSpecification(String resultDescription, String description, HelperFunctionArgument ... arguments){

		this.arguments = Arrays.asList(arguments);
		this.resultDescription = resultDescription;
		this.description = description;
	}


	/**
	 *  gets description about what a given function can do
	 *  
	 * @return the description of a function
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * get the list of arguments that a given function takes.
	 * @return arguments argument list of a function
	 */
	public List<HelperFunctionArgument> getArguments() {
		return arguments;
	}

	/**
	 * get the description about the result of a function call.
	 * 
	 * @return result description of a function
	 */
	public String getResultDescription() {
		return resultDescription;
	}
}
