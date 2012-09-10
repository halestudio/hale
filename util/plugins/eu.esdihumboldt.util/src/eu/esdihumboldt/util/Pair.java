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

import net.jcip.annotations.Immutable;

/**
 * An object pair
 * 
 * @param <F> the type of the first object
 * @param <S> the type of the second object
 * @author Simon Templer
 */
@Immutable
public class Pair<F, S> {

	private final F first;

	private final S second;

	/**
	 * Create a pair with the given objects
	 * 
	 * @param first the first object
	 * @param second the second object
	 */
	public Pair(F first, S second) {
		super();
		this.first = first;
		this.second = second;
	}

	/**
	 * Get the first object
	 * 
	 * @return the first object
	 */
	public F getFirst() {
		return first;
	}

	/**
	 * Get the second object
	 * 
	 * @return the second object
	 */
	public S getSecond() {
		return second;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		}
		else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		}
		else if (!second.equals(other.second))
			return false;
		return true;
	}

}
