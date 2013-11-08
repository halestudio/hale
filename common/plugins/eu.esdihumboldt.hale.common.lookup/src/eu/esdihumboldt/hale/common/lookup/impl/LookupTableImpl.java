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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.jcip.annotations.Immutable;

import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.lookup.LookupTable;

/**
 * Simple lookup table implementation based on a {@link Map}.
 * 
 * @author Simon Templer
 */
@Immutable
public class LookupTableImpl implements LookupTable {

	private final Map<Value, Value> table;

	/**
	 * Create a new lookup table based on the given map.
	 * 
	 * @param table the lookup table map
	 */
	public LookupTableImpl(Map<Value, Value> table) {
		super();
		// ignore null values, so their entries don't show up in any methods
		this.table = new LinkedHashMap<Value, Value>(Maps.filterValues(table, Predicates.notNull()));
	}

	@Override
	public Value lookup(Value key) {
		return table.get(key);
	}

	@Override
	public Set<Value> getKeys() {
		return Collections.unmodifiableSet(table.keySet());
	}

	@Override
	public ListMultimap<Value, Value> reverse() {
		ListMultimap<Value, Value> result = ArrayListMultimap.create();

		for (Entry<Value, Value> entry : table.entrySet()) {
			result.put(entry.getValue(), entry.getKey());
		}

		return result;
	}

	@Override
	public Map<Value, Value> asMap() {
		return Collections.unmodifiableMap(table);
	}

}
