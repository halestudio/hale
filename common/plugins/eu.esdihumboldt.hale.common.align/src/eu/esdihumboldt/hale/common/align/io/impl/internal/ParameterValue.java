/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.io.impl.internal;

/**
 * Combines a parameter name and value
 * 
 * @author Simon Templer
 */
public class ParameterValue {

	private String name;

	private String value;

	/**
	 * Default constructor
	 */
	public ParameterValue() {
		super();
	}

	/**
	 * Creates a parameter value an initializes it with the given name and value
	 * 
	 * @param name the parameter name
	 * @param value the parameter value
	 */
	public ParameterValue(String name, String value) {
		this.name = name;
		this.value = value;
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

}
