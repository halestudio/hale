/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Return value class for property transformations which want to return multiple
 * values.
 * 
 * @author Kai Schwierczek
 */
public class MultiValue extends ArrayList<Object> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public MultiValue() {
		super();
	}

	/**
	 * Constructs a list containing the elements of the specified collection, in
	 * the order they are returned by the collection's iterator.
	 * 
	 * @param c the collection whose elements are to be placed into this list
	 */
	public MultiValue(Collection<? extends Object> c) {
		super(c);
	}

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity the initial capacity of the list
	 */
	public MultiValue(int initialCapacity) {
		super(initialCapacity);
	}
}
