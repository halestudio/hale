/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.commons.goml.oml.ext;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.specification.cst.align.ext.IValueClass;
import eu.esdihumboldt.specification.cst.align.ext.IValueExpression;

/**
 * A {@link ValueClass} is a collection of {@link ValueExpression}s. Helper
 * class to store recurring groups of values. Can be referred to from more than
 * one Cell.
 * 
 * @author Marian de Vries
 * @partner 08 / Delft University of Technology
 * @version $Id$
 */

public class ValueClass implements IValueClass {

	private List<IValueExpression> value;

	/**
	 * Sets the value of the value property.
	 * 
	 * @param List
	 *            of value properties
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
	public String getResource() {
		return resource;
	}

	/**
	 * Sets the value of the resource property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
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
	public String getAbout() {
		return about;
	}

	/**
	 * Sets the value of the about property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setAbout(String value) {
		this.about = value;
	}

	@Override
	public String toString() {
		return "ValueClass [about=" + about + ", resource=" + resource
				+ ", value=" + value + "]";
	}

}
