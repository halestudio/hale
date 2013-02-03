/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.lookup;

import java.util.Set;

/**
 * Lookup table interface.
 * 
 * @param <K> the lookup key type
 * @param <V> the type of values retrieved from the table
 * @author Simon Templer
 */
public interface LookupTable<K, V> {

	/**
	 * Look up a value associated to a given key.
	 * 
	 * @param key the key
	 * @return the associated value or <code>null</code>
	 */
	public V lookup(K key);

	/**
	 * Get all keys available in the lookup table.
	 * 
	 * @return the set of lookup keys
	 */
	public Set<K> getKeys();

//	public Class<K> getKeyType();

}
