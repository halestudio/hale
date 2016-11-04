/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A list where the entries are sorted by dependencies, the dependencies to an
 * entry are before the entry
 * 
 * @param <T> the entry type
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DependencyOrderedList<T> {

	private final List<T> list;

	/**
	 * Create a new list
	 * 
	 * <b>Attention:</b> Due to the use of recursion a huge dependency set may
	 * result in a StackOverflowError.
	 * 
	 * @param dependencies the dependency map
	 */
	public DependencyOrderedList(Map<T, Set<T>> dependencies) {
		list = new ArrayList<T>(dependencies.size());

		Set<T> alreadyInserted = new HashSet<T>();

		for (Entry<T, Set<T>> entry : dependencies.entrySet()) {
			if (!alreadyInserted.contains(entry.getKey())) {
				insert(entry.getKey(), dependencies, alreadyInserted);
			}
		}
	}

	/**
	 * Insert an object into the list
	 * 
	 * @param object the object to insert
	 * @param dependencies the map with all dependencies
	 * @param alreadyInserted the objects that were already inserted
	 */
	private void insert(T object, Map<T, Set<T>> dependencies, Set<T> alreadyInserted) {
		Set<T> objectDependencies = dependencies.get(object);

		// mark object as inserted to prevent cycle loops
		alreadyInserted.add(object);

		// initialize last index
		int lastIndex = -1;

		if (objectDependencies != null) {
			// find all dependencies that have not yet been inserted
			for (T dependency : objectDependencies) {
				if (!alreadyInserted.contains(dependency)) { // prevent cycle
																// loops
					int index = find(dependency);

					if (index < 0) {
						// insert dependency
						insert(dependency, dependencies, alreadyInserted);
					}
				}
			}

			// determine the index to insert the object at
			for (T dependency : objectDependencies) {
				int index = find(dependency);
				lastIndex = Math.max(lastIndex, index);
			}
		}

		// the minimum index the item has to be inserted at
		int minInsertIndex = lastIndex + 1;

		// insert the object
		list.add(minInsertIndex, object);
	}

	/**
	 * Find an object in the list
	 * 
	 * @param object the object to find
	 * @return the object's index or <code>-1</code> if there is no such object
	 *         in the list
	 */
	private int find(T object) {
		return list.indexOf(object);
	}

	/**
	 * Add an object where it is sure that none of the others depends on it
	 * 
	 * @param object the object to append
	 */
	public void append(T object) {
		list.add(object);
	}

	/**
	 * Get an {@link Iterable} over the list's items
	 * 
	 * @return the list's items
	 */
	public Iterable<T> getItems() {
		return list;
	}

	/**
	 * Get the internal list, there shouldn't be made any changes to this list.
	 * 
	 * @return the internal list
	 */
	public List<T> getInternalList() {
		return list;
	}

}
