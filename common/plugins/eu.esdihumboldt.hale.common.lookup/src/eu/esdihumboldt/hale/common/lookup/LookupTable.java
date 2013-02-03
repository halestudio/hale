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

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Lookup table interface.
 * 
 * @author Simon Templer
 */
public interface LookupTable {

	/**
	 * Look up a value associated to a given key.
	 * 
	 * @param key the key
	 * @return the associated value or <code>null</code>
	 */
	public Value lookup(Value key);

	/**
	 * Get all keys available in the lookup table.
	 * 
	 * @return the set of lookup keys
	 */
	public Set<Value> getKeys();

	/**
	 * Get the reverse of the lookup. Note that in the reverse representation
	 * there may be multiple values for a key.
	 * 
	 * @return the reverse representation of the lookup table
	 */
	public ListMultimap<Value, Value> reverse();

}
