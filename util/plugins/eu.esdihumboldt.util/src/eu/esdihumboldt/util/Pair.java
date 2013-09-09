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
	 * Pair creation helper.
	 * @param <F> the type of the first element in the pair
	 * @param <S> the type of the second element
	 * @param first the first object
	 * @param second the second object
	 * @return a new pair
	 */
	public static <F, S> Pair<F, S> make(F first, S second) {
		return new Pair<F, S>(first, second);
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
