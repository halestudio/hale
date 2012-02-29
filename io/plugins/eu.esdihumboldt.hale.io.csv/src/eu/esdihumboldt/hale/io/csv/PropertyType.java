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

package eu.esdihumboldt.hale.io.csv;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Type class for the property types
 * 
 * @author Kevin Mais
 */
public interface PropertyType {

	/**
	 * Converts the value into the property type of the class
	 * 
	 * @param value the value to convert
	 * @return the converted value
	 */
	public Object convertFromField(String value);
	
	/**
	 * Getter for the type definition
	 * 
	 * @return the TypeDefinition of the property type
	 */
	public TypeDefinition getTypeDefinition();
	
}
