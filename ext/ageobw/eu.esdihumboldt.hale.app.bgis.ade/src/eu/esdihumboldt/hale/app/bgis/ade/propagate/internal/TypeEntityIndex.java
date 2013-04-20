/*
 * Copyright (c) 2013 Fraunhofer IGD
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.propagate.internal;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Index with combined type and {@link Entity} key. Entities are indexed based
 * on their identity.
 * 
 * @author Simon Templer
 * @param <T> the type of object stored
 */
public class TypeEntityIndex<T> {

	private final Map<TypeDefinition, IdentityHashMap<Entity, T>> index = new HashMap<TypeDefinition, IdentityHashMap<Entity, T>>();

	/**
	 * Put an object into the index.
	 * 
	 * @param type the type key
	 * @param entity the entity key
	 * @param object the object to store
	 */
	public void put(TypeDefinition type, Entity entity, T object) {
		IdentityHashMap<Entity, T> objects = index.get(type);
		if (objects == null) {
			objects = new IdentityHashMap<Entity, T>();
			index.put(type, objects);
		}
		objects.put(entity, object);
	}

	/**
	 * Get an object from the index.
	 * 
	 * @param type the type key
	 * @param entity the entity key
	 * @return the stored object at the given keys or <code>null</code>
	 */
	public T get(TypeDefinition type, Entity entity) {
		IdentityHashMap<Entity, T> objects = index.get(type);
		if (objects != null) {
			return objects.get(entity);
		}
		return null;
	}

}
