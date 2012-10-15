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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model;

import net.jcip.annotations.Immutable;

/**
 * Class to represent the value of a transformation parameter.
 * 
 * @author Kai Schwierczek
 */
@Immutable
public class ParameterValue {

	/**
	 * The name of the default type of value.
	 */
	public static final String DEFAULT_TYPE = "default";

	private final String type;
	private final String value;

	/**
	 * Constructor specifying the type and the value.
	 * 
	 * @param type the type of the value
	 * @param value the value
	 */
	public ParameterValue(String type, String value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * Constructor specifying the value only. type will be default.
	 * 
	 * @param value the value
	 */
	public ParameterValue(String value) {
		this(DEFAULT_TYPE, value);
	}

	/**
	 * Returns the type of the value. Either "default" or some script id.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
