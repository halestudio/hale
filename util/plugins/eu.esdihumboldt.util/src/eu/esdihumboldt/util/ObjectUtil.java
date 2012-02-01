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

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

/**
 * General utility methods.
 * @author Simon Templer
 */
public class ObjectUtil {

	/**
	 * Determines if the given objects are equal, in turn descending into
	 * {@link Iterable}s and arrays and checking if the elements are equal
	 * (in order).
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return if both objects are equal
	 */
	public static boolean deepIterableEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		
		Iterable<?> iterable1 = asIterable(o1);
		Iterable<?> iterable2 = asIterable(o1);
		if (iterable1 != null && iterable2 != null) {
			if (Iterables.size(iterable1) == Iterables.size(iterable2)) { // size check
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

	private static Iterable<?> asIterable(Object object) {
		if (object instanceof Iterable<?>) {
			return (Iterable<?>) object;
		}
		if (object.getClass().isArray()) {
			return Arrays.asList((Object[]) object);
		}
		return null;
	}
	
}
