/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.context.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * Experimental implementation of a multimap where values are stored in an
 * identity based set/map.
 * 
 * This implementation is not complete and may not adhere to all clauses of the
 * {@link Multimap} contract.
 * 
 * @author Simon Templer
 * @param <K> the key type
 * @param <V> the value type
 */
public class IdentityHashMultimap<K, V> implements Multimap<K, V> {

	private final Map<K, IdentityHashMap<V, Void>> map = new HashMap<>();

	@Override
	public int size() {
		int size = 0;
		for (IdentityHashMap<V, Void> val : map.values()) {
			size += val.size();
		}
		return size;
	}

	@Override
	public boolean isEmpty() {
		for (IdentityHashMap<V, Void> val : map.values()) {
			if (!val.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key) && !map.get(key).isEmpty();
	}

	@Override
	public boolean containsValue(Object value) {
		for (IdentityHashMap<V, Void> val : map.values()) {
			if (!val.containsKey(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsEntry(Object key, Object value) {
		IdentityHashMap<V, Void> val = map.get(key);
		if (val != null) {
			return val.containsKey(value);
		}
		return false;
	}

	@Override
	public boolean put(K key, V value) {
		IdentityHashMap<V, Void> val = map.get(key);
		if (val == null) {
			val = new IdentityHashMap<>();
			map.put(key, val);
			val.put(value, null);
			return true;
		}
		else {
			if (val.containsKey(value)) {
				return false;
			}
			else {
				val.put(value, null);
				return true;
			}
		}

	}

	@Override
	public boolean remove(Object key, Object value) {
		IdentityHashMap<V, Void> val = map.get(key);
		if (val != null) {
			if (val.containsKey(value)) {
				val.remove(value);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean putAll(K key, Iterable<? extends V> values) {
		IdentityHashMap<V, Void> val = map.get(key);
		if (val == null) {
			val = new IdentityHashMap<>();
			map.put(key, val);
		}

		boolean changed = false;
		for (V value : values) {
			if (!val.containsKey(value)) {
				val.put(value, null);
				changed = true;
			}
		}

		return changed;
	}

	@Override
	public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
		// FIXME can be done more efficient
		boolean changed = false;
		for (Map.Entry<? extends K, ? extends V> entry : multimap.entries()) {
			changed |= put(entry.getKey(), entry.getValue());
		}
		return changed;
	}

	@Override
	public Collection<V> replaceValues(K key, Iterable<? extends V> values) {
		IdentityHashMap<V, Void> old = map.get(key);

		IdentityHashMap<V, Void> val = new IdentityHashMap<>();
		map.put(key, val);

		for (V value : values) {
			val.put(value, null);
		}

		if (old == null) {
			return Collections.emptySet();
		}
		else {
			return old.keySet();
		}
	}

	@Override
	public Collection<V> removeAll(Object key) {
		IdentityHashMap<V, Void> old = map.get(key);

		map.remove(key);

		if (old == null) {
			return Collections.emptySet();
		}
		else {
			return old.keySet();
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Collection<V> get(K key) {
		IdentityHashMap<V, Void> val = map.get(key);
		if (val == null) {
			val = new IdentityHashMap<>();
			map.put(key, val);
		}
		return val.keySet();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Multiset<K> keys() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Entry<K, V>> entries() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<K, Collection<V>> asMap() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
