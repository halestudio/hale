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

package eu.esdihumboldt.hale.common.align.custom;

import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;

/**
 * Default implementation of a custom function parameter.
 * 
 * @author Simon Templer
 */
public class MinimalParameter implements ParameterDefinition {

	private String name;
	private int minOccurrence;
	private int maxOccurrence;
	private String description;
	private String displayName;

	/**
	 * Default constructor.
	 */
	public MinimalParameter() {
		super();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other the object to copy
	 */
	public MinimalParameter(MinimalParameter other) {
		super();
		setName(other.getName());
		setMinOccurrence(other.getMinOccurrence());
		setMaxOccurrence(other.getMaxOccurrence());
		if (other.displayName != null) {
			setDisplayName(other.getDisplayName());
		}
		setDescription(other.getDescription());
	}

	/**
	 * @param name the parameter name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param minOccurrence the parameter minimum occurrence to set
	 */
	public void setMinOccurrence(int minOccurrence) {
		this.minOccurrence = minOccurrence;
	}

	/**
	 * @param maxOccurrence the parameter maximum occurrence to set
	 */
	public void setMaxOccurrence(int maxOccurrence) {
		this.maxOccurrence = maxOccurrence;
	}

	@Override
	public int getMinOccurrence() {
		return minOccurrence;
	}

	@Override
	public int getMaxOccurrence() {
		return maxOccurrence;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String getDisplayName() {
		if (displayName != null) {
			return displayName;
		}
		return (getName() == null) ? ("") : (getName());
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
