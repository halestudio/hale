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

package eu.esdihumboldt.hale.common.schema.model;

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;

/**
 * A type index holds a set of type definitions and a corresponding name index.
 * @author Simon Templer
 */
public interface TypeIndex {
	
	/**
	 * Get all types in the index
	 *  
	 * @return all type definitions
	 */
	public Collection<? extends TypeDefinition> getTypes();
	
	/**
	 * Get the type with the given name
	 * 
	 * @param name the type name
	 * @return the type definition or <code>null</code> if no type with the
	 *   given name exists in the index
	 */
	public TypeDefinition getType(QName name);
	
	/**
	 * Get all mappable types, i.e. types that are flagged with an enabled
	 * {@link MappableFlag}
	 *  
	 * @return the mappable types
	 */
	public Collection<? extends TypeDefinition> getMappableTypes();

	/**
	 * Toggles the mappable flag of the given types.
	 * 
	 * @param types the types to toggle
	 */
	public void toggleMappable(Collection<? extends TypeDefinition> types);
}
