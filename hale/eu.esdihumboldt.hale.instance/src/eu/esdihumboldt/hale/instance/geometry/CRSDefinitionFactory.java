// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2010 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale.instance.geometry;

import eu.esdihumboldt.hale.schema.geometry.CRSDefinition;

/**
 * Provides support for creating a {@link CRSDefinition} from a definition
 * string and vice versa.
 * @param <T> the CRS definition type
 * 
 * @author Simon Templer
 */
public interface CRSDefinitionFactory<T extends CRSDefinition> {
	
	/**
	 * Get the factory identifier. It is used to associate a definition string
	 * to the factory.
	 * @return the factory identifier
	 */
	public String getIdentifier();
	
	/**
	 * Get the class of the supported {@link CRSDefinition}.
	 * 
	 * @return the CRS definition class supported by this factory
	 */
	public Class<T> getDefinitionClass();
	
	/**
	 * Parse the given definition string and create a CRS definition instance.
	 * 
	 * @param value the definition string to parse
	 * @return the CRS definition instance or <code>null</code>
	 */
	public T parse(String value);
	
	/**
	 * Represent the given CRS definition as a definition string, so that it 
	 * can be used to again create a CRS definition instance using 
	 * {@link #parse(String)}.
	 *   
	 * @param crsDef the CRS definition to create a string representation for
	 * @return the string representation of the CRS definition
	 */
	public String asString(T crsDef);

}
