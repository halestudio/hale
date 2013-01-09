/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

/**
 * Stands for an alignment modifier.
 * 
 * @author Kai Schwierczek
 */
public class ModifierBean {

	/**
	 * Name to specify that a cell was deactivated.
	 */
	public static final String DEACTIVATE_CELL = "deactivateCell";

	private String name;
	private String value;

	/**
	 * Default constructor.
	 */
	public ModifierBean() {
	}

	/**
	 * Constructor using the specified name and value.
	 * 
	 * @param name the name to set
	 * @param value the value to set
	 */
	public ModifierBean(String name, String value) {
		this.name = name;
		this.value = value;
	}

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
