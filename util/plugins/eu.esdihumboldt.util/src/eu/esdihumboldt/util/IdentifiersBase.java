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

package eu.esdihumboldt.util;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A base class for identifier handling
 * 
 * @param <T> the type of objects that shall be identified
 * @author Simon Thum
 */
public abstract class IdentifiersBase<T> {

	/**
	 * maps identifiers to objects
	 */
	protected final Map<String, T> objects = new HashMap<String, T>();

	/**
	 * maps objects to identifiers
	 */
	protected final Map<T, String> ids;

	/**
	 * @param useEquals whether to use equality or object identity as identifier
	 *            scope
	 */
	protected IdentifiersBase(boolean useEquals) {
		if (useEquals) {
			ids = new HashMap<T, String>();
		}
		else {
			ids = new IdentityHashMap<T, String>();
		}
	}

	/**
	 * puts a object-identifier pair into the maps, no checks or what.
	 * 
	 * @param object the object
	 * @param id the identifier
	 */
	protected void putObjectIdentifier(T object, String id) {
		objects.put(id, object);
		ids.put(object, id);
	}

	/**
	 * Get the id of the given object. May generate an Id.
	 * 
	 * @param object the object
	 * @return the id of the object
	 */
	public String getId(T object) {
		return ids.get(object);
	}

	/**
	 * Get the id of the given object iff it has an Id.
	 * 
	 * @param object the object
	 * @return the id of the object
	 */
	public String fetchId(T object) {
		return ids.get(object);
	}

	/**
	 * Get the object with the given id
	 * 
	 * @param id the id
	 * @return the object or null if the id has no associated object
	 */
	public final T getObject(String id) {
		return objects.get(id);
	}

	/**
	 * Get the set of objects
	 * 
	 * @return the set of all objects with an associated identifier
	 */
	public final Set<T> getObjects() {
		return ids.keySet();
	}

	/**
	 * Get the ids of all objects
	 * 
	 * @return the set of all ids
	 */
	public final Set<String> getIds() {
		return objects.keySet();
	}

}