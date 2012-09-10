// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2010 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.util.definition;

/**
 * Provides support for creating an object of a certain type from a definition
 * string and vice versa.
 * 
 * @param <T> the object type
 * 
 * @author Simon Templer
 */
public interface ObjectDefinition<T> {

	/**
	 * Get the factory identifier. The identifier must be unique for object
	 * factories that have compatible supported types. It is used to associate a
	 * definition string to the factory. The identifier may not contain a
	 * <code>:</code>
	 * 
	 * @return the factory identifier
	 */
	public String getIdentifier();

	/**
	 * Get the class of the supported object.
	 * 
	 * @return the object class supported by this factory
	 */
	public Class<T> getObjectClass();

	/**
	 * Parse the given definition string and create an object instance.
	 * 
	 * @param value the definition string to parse
	 * @return the CRS definition instance or <code>null</code>
	 */
	public T parse(String value);

	/**
	 * Represent the given object as a definition string, so that it can be used
	 * to again create an object instance using {@link #parse(String)}.
	 * 
	 * @param object the object to create a string representation for
	 * @return the string representation of the object
	 */
	public String asString(T object);

}
