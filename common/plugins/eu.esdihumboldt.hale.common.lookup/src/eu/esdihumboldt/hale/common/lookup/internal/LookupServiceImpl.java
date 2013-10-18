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

package eu.esdihumboldt.hale.common.lookup.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.common.lookup.LookupService;
import eu.esdihumboldt.hale.common.lookup.LookupTableInfo;

/**
 * Default lookup service implementation.
 * 
 * @author Simon Templer
 */
public class LookupServiceImpl implements LookupService {

	private final Map<String, LookupTableInfo> tables = new HashMap<String, LookupTableInfo>();

	@Override
	public void registerTable(String id, LookupTableInfo tableInfo) throws IllegalArgumentException {
		synchronized (tables) {
			if (tables.containsKey(id))
				throw new IllegalArgumentException("Lookup table identifier already taken.");
			tables.put(id, tableInfo);
		}
	}

	@Override
	public LookupTableInfo getTable(String id) {
		synchronized (tables) {
			return tables.get(id);
		}
	}

	@Override
	public Set<String> getTableIDs() {
		synchronized (tables) {
			return new HashSet<String>(tables.keySet());
		}
	}

	@Override
	public boolean removeTable(String id) {
		synchronized (tables) {
			return tables.remove(id) != null;
		}
	}
}
