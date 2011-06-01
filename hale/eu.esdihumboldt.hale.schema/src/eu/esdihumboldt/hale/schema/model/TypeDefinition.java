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

package eu.esdihumboldt.hale.schema.model;

import java.util.Collection;

import javax.xml.namespace.QName;

/**
 * Represents a type definition
 * @author Simon Templer
 */
public interface TypeDefinition extends Definition<TypeConstraint> {

	/**
	 * Get the properties declared by the type
	 * 
	 * @return the definitions of the declared properties
	 */
	public Iterable<? extends PropertyDefinition> getDeclaredProperties();
	
	/**
	 * Get all properties that an instance of the type may have. Usually these
	 * are the declared properties and the super type properties.
	 * 
	 * @return the property definitions
	 */
	public Collection<? extends PropertyDefinition> getProperties();
	
	/**
	 * Get the super type
	 * 
	 * @return the super type, may be <code>null</code>
	 */
	public TypeDefinition getSuperType();
	
	/**
	 * Get the sub types
	 * 
	 * @return the list of sub types, may not be modified
	 */
	public Collection<? extends TypeDefinition> getSubTypes();
	
	/**
	 * Get the property with the given name
	 * 
	 * @param name the property name
	 * 
	 * @return the property definition or <code>null</code> if no property with
	 *   the given name is available
	 */
	public PropertyDefinition getProperty(QName name);
	
}
