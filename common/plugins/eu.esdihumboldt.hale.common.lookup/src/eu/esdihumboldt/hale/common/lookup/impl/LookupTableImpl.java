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

package eu.esdihumboldt.hale.common.lookup.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.Immutable;
import eu.esdihumboldt.hale.common.lookup.LookupTable;

/**
 * Simple lookup table implementation based on a {@link Map}.
 * 
 * @param <K> the lookup key type
 * @param <V> the type of values retrieved from the table
 * @author Simon Templer
 */
@Immutable
public class LookupTableImpl<K, V> implements LookupTable<K, V> {

	private final Map<K, V> table;

	/**
	 * Create a new lookup table based on the given map.
	 * 
	 * @param table the lookup table map
	 */
	public LookupTableImpl(Map<K, V> table) {
		super();
		this.table = new HashMap<>(table);
	}

	@Override
	public V lookup(K key) {
		return table.get(key);
	}

	@Override
	public Set<K> getKeys() {
		return Collections.unmodifiableSet(table.keySet());
	}

}
