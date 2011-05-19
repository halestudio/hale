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

package eu.esdihumboldt.hale.instance.model;

import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Represents an instance of a type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface Instance {
	
	/**
	 * Get the definition of the type associated with the instance
	 * 
	 * @return the instance's type definition
	 */
	public TypeDefinition getType();
	
	/**
	 * Get the instance value.<br>
	 * <b>NOTE:</b> This is needed for instance for XML elements with text content
	 * and attributes. It may only be a simple value. 
	 * 
	 * @return the instance value if it is defined, otherwise <code>null</code>
	 */
	public Object getValue();
	
	/**
	 * Get the values for the property with the given name. Values can also
	 * be instances.
	 * 
	 * @param propertyName the property name
	 * @return the property values, may be <code>null</code> if none exist
	 */
	public Object[] getProperty(String propertyName);
	
	/**
	 * Get the property names for which any values exist.
	 * Especially helpful when there is (for whatever reason) no type definition
	 * associated to the instance.
	 * 
	 * @return the list of property names with associated values
	 */
	public Iterable<String> getPropertyNames();

}
