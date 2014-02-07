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

package eu.esdihumboldt.hale.common.core.io;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of {@link Value} properties with a string key that can itself be
 * represented as complex value.
 * 
 * @author Simon Templer
 */
public class ValueProperties extends HashMap<String, Value> {

	private static final long serialVersionUID = -7763877695763033028L;

	/**
	 * @see HashMap#HashMap()
	 */
	public ValueProperties() {
		super();
	}

	/**
	 * @see HashMap#HashMap(int, float)
	 */
	public ValueProperties(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * @see HashMap#HashMap(int)
	 */
	public ValueProperties(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * @see HashMap#HashMap(Map)
	 */
	public ValueProperties(Map<? extends String, ? extends Value> m) {
		super(m);
	}

	/**
	 * Creates a {@link Value} wrapping the properties.
	 * 
	 * @return the {@link Value} representation of the properties
	 */
	public Value toValue() {
		return Value.complex(this);
	}

	/**
	 * Get the value for the given property.
	 * 
	 * @param property the property name
	 * @return the value or the NULL value, never <code>null</code>
	 */
	public Value getSafe(String property) {
		Value val = get(property);
		if (val == null) {
			val = Value.NULL;
		}
		return val;
	}

}
