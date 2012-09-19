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

package eu.esdihumboldt.hale.io.oml.internal.goml.oml.ext;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueClass;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ext.IValueExpression;

/**
 * A {@link ValueClass} is a collection of {@link ValueExpression}s. Helper
 * class to store recurring groups of values. Can be referred to from more than
 * one Cell.
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 */
@SuppressWarnings("javadoc")
public class ValueClass implements IValueClass {

	private List<IValueExpression> value;

	/**
	 * Sets the value of the value property.
	 * 
	 * @param List of value properties
	 * 
	 */

	public void setValue(List<IValueExpression> value) {
		this.value = value;
	}

	private String resource;

	private String about;

	/**
	 * Gets the value of the value property.
	 * 
	 */
	@Override
	public List<IValueExpression> getValue() {
		if (value == null) {
			value = new ArrayList<IValueExpression>();
		}
		return this.value;
	}

	/**
	 * Gets the value of the resource property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	@Override
	public String getResource() {
		return resource;
	}

	/**
	 * Sets the value of the resource property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setResource(String value) {
		this.resource = value;
	}

	/**
	 * Gets the value of the about property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	@Override
	public String getAbout() {
		return about;
	}

	/**
	 * Sets the value of the about property.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setAbout(String value) {
		this.about = value;
	}

	@Override
	public String toString() {
		return "ValueClass [about=" + about + ", resource=" + resource + ", value=" + value + "]";
	}

}
