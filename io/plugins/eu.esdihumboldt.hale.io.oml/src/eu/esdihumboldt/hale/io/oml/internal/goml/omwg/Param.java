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

package eu.esdihumboldt.hale.io.oml.internal.goml.omwg;

/**
 * This class represents <xs:complexType name="ParamType" >.
 * 
 * @author Marian de Vries
 * @partner 08 / Delft Universtiy of Technology
 */
@SuppressWarnings("javadoc")
public class Param {

	/**
	 * the name of this {@link Param}.
	 */
	private String name;

	/**
	 * TODO: Check whether {@link Param} value should be typed stronger.
	 */
	private String value;

	// constuctors .............................................................

	/**
	 * @param name
	 * @param value
	 */
	public Param(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	// getters / setters .......................................................

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
