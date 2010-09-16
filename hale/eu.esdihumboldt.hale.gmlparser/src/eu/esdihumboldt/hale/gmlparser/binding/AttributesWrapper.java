/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlparser.binding;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps a value and carries information about its attributes
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class AttributesWrapper {
	
	private final Object value;
	
	private final Map<String, String> attributes = new HashMap<String, String>();

	/**
	 * Constructor
	 * 
	 * @param value the value to wrap
	 */
	public AttributesWrapper(Object value) {
		super();
		this.value = value;
	}

	/**
	 * Adds an attribute
	 * 
	 * @param name the attribute name
	 * @param value the attribute value
	 */
	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return value.toString();
	}

}
