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

package eu.esdihumboldt.cst.functions.groovy.helper.spec.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.cst.functions.groovy.helper.spec.Argument;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification;

/**
 * It denotes the specification details for helper functions.
 * 
 * @author sameer sheikh
 */
public class HelperFunctionSpecification implements Specification {

	private String description;
	private final List<Argument> arguments;
	private String resultDescription;

	/**
	 * parameterized constructor
	 * 
	 * @param resultDescription description about the return of the function
	 * @param description Description about a function
	 * @param arguments arguments that can be passed into the function
	 */
	public HelperFunctionSpecification(String resultDescription, String description,
			Argument... arguments) {

		this.arguments = Arrays.asList(arguments);
		this.resultDescription = resultDescription;
		this.description = description;
	}

	/**
	 * Default constructor.
	 */
	public HelperFunctionSpecification() {
		super();
		this.arguments = new ArrayList<>();
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param resultDescription the resultDescription to set
	 */
	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	/**
	 * Add an additional argument to the function.
	 * 
	 * @param argument the argument to add
	 */
	public void addArgument(Argument argument) {
		arguments.add(argument);
	}

	/**
	 * gets description about what a given function can do
	 * 
	 * @return the description of a function
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * get the list of arguments that a given function takes.
	 * 
	 * @return arguments argument list of a function
	 */
	@Override
	public List<Argument> getArguments() {
		return Collections.unmodifiableList(arguments);
	}

	/**
	 * get the description about the result of a function call.
	 * 
	 * @return result description of a function
	 */
	@Override
	public String getResultDescription() {
		return resultDescription;
	}
}
