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

	private Object defaultValue;

	/**
	 * @param name name of an argument
	 * @param description description about an argument
	 */
	public HelperFunctionArgument(String name, String description) {
		this(name, description, null);

	}

	/**
	 * Parameterized constructor
	 * 
	 * @param name name of the argument
	 * @param description description of the argument
	 * @param defaultValue the default value of an argument
	 */
	public HelperFunctionArgument(String name, String description, Object defaultValue) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
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

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

}
