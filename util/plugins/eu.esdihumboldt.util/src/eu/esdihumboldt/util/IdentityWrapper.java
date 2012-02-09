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
 * Class that defines its {@link #equals(Object)} and {@link #hashCode()}
 * based on the contained values identity.
 * @param <T> the value type
 * @author Simon Templer
 */
@Immutable
public class IdentityWrapper<T> {

	private final T value;

	/**
	 * Create a wrapper for the given value.
	 * @param value the value to wrap, if the value is an
	 *   {@link IdentityWrapper}, its value will be extracted and used as the
	 *   value to wrap
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
