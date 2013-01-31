/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Combines a parameter name and value
 * 
 * @author Simon Templer
 */
public class ParameterValueBean {

	private String name;

	private String type = ParameterValue.DEFAULT_TYPE;

	private String value;

	/**
	 * Default constructor
	 */
	public ParameterValueBean() {
		super();
	}

	/**
	 * Creates a parameter value an initializes it with the given name and value
	 * with default type
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public ParameterValueBean(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Creates a parameter value an initializes it with the given name and value
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public ParameterValueBean(String name, ParameterValue value) {
		this.name = name;
		this.type = value.getType();
		this.value = value.as(String.class);
	}

	/**
	 * Get the parameter name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the parameter name
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the parameter type
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the parameter type
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Get the parameter value
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the parameter value
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Creates a {@link ParameterValue} of this bean.
	 * 
	 * @return the created parameter value
	 */
	public ParameterValue createParameterValue() {
		return new ParameterValue(type, Value.of(value));
	}
}
