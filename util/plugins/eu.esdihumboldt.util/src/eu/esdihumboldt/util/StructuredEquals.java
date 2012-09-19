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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

/**
 * StructuredEquals provides methods for equals and hashCode implementations for
 * complex structures.
 * 
 * @author Simon Templer
 */
public class StructuredEquals {

	/**
	 * Determines if the given objects are equal, in turn descending into
	 * {@link Iterable}s and arrays and checking if the elements are equal (in
	 * order).
	 * 
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return if both objects are equal
	 * @see #deepIterableHashCode(Object)
	 */
	public boolean deepIterableEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}

		Iterable<?> iterable1 = asIterable(o1);
		Iterable<?> iterable2 = asIterable(o2);
		if (iterable1 != null && iterable2 != null) {
			if (Iterables.size(iterable1) == Iterables.size(iterable2)) { // size
																			// check
				Iterator<?> it1 = iterable1.iterator();
				Iterator<?> it2 = iterable2.iterator();
				while (it1.hasNext() || it2.hasNext()) {
					try {
						if (!deepIterableEquals(it1.next(), it2.next())) {
							return false;
						}
					} catch (NoSuchElementException e) {
						return false;
					}
				}

				return true;
			}

			return false;
		}
		else {
			return Objects.equal(o1, o2);
		}
	}

	/**
	 * Get the hash code for all contained objects, descending into
	 * {@link Iterable}s and arrays.
	 * 
	 * @param object the object to determine the hash code from
	 * @return the hash code
	 * @see #deepIterableEquals(Object, Object)
	 */
	public int deepIterableHashCode(Object object) {
		return Arrays.hashCode(collectObjects(object).toArray());
	}

	/**
	 * Collect all objects contained in an {@link Iterable} or array and in
	 * their elements.
	 * 
	 * @param object the object to collect objects on
	 * @return the collected objects
	 */
	public Collection<?> collectObjects(Object object) {
		Iterable<?> iterable = asIterable(object);
		if (iterable == null) {
			return Collections.singleton(object);
		}
		else {
			Collection<Object> result = new ArrayList<Object>();
			for (Object child : iterable) {
				result.addAll(collectObjects(child));
			}
			return result;
		}
	}

	/**
	 * Returns an iterable for the given objects contents, or null if it does
	 * not contain anything that needs to be compared.
	 * 
	 * @param object the object in question
	 * @return an iterable for the given object
	 */
	protected Iterable<?> asIterable(Object object) {
		if (object == null)
			return null;
		if (object instanceof Iterable<?>) {
			return (Iterable<?>) object;
		}
		if (object.getClass().isArray()) {
			return Arrays.asList((Object[]) object);
		}
		return null;
	}
}
