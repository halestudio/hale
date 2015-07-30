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

import eu.esdihumboldt.cst.functions.groovy.helper.spec.Argument;

/**
 * Denotes the helper function argument
 * 
 * @author Sameer Sheikh
 */
public class HelperFunctionArgument implements Argument {

	private String name;
	private String description;

	/**
	 * Parameterized constructor
	 * 
	 * @param name name of the argument
	 * @param description description of the argument
	 */
	public HelperFunctionArgument(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * Default constructor.
	 */
	public HelperFunctionArgument() {
		super();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
