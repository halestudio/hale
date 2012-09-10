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

package eu.esdihumboldt.commons.goml.omwg;

/**
 * This class represents <xs:complexType name="ParamType" >.
 * 
 * @author Marian de Vries
 * @partner 08 / Delft Universtiy of Technology
 * @version $Id$
 */
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
	 * @param name
	 *            the name to set
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
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
