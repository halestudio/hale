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
 * Class that defines its {@link #equals(Object)} and {@link #hashCode()} based
 * on the contained values identity.
 * 
 * @param <T> the value type
 * @author Simon Templer
 */
@Immutable
public class IdentityWrapper<T> {

	private final T value;

	/**
	 * Create a wrapper for the given value.
	 * 
	 * @param value the value to wrap, if the value is an
	 *            {@link IdentityWrapper}, its value will be extracted and used
	 *            as the value to wrap
	 */
	@SuppressWarnings("unchecked")
	public IdentityWrapper(T value) {
		super();
		if (value instanceof IdentityWrapper<?>) {
			this.value = ((IdentityWrapper<T>) value).getValue();
		}
		else {
			this.value = value;
		}
	}

	/**
	 * Get the contained value.
	 * 
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdentityWrapper<?>) {
			return value == ((IdentityWrapper<?>) obj).value;
		}
		return false;
	}

}
