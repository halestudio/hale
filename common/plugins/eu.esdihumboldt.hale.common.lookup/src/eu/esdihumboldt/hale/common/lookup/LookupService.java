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
 * Service holding the available lookup tables.
 * 
 * @author Simon Templer
 */
public interface LookupService {

	/**
	 * Register a lookup table with the service.
	 * 
	 * @param id the unique lookup table ID
	 * @param tableInfo the lookup table plus meta information
	 * @throws IllegalArgumentException if the given identifier is already used
	 */
	public void registerTable(String id, LookupTableInfo tableInfo) throws IllegalArgumentException;

	/**
	 * Get the lookup table registered with the given identifier.
	 * 
	 * @param id the lookup table identifier
	 * @return the lookup table plus meta information if a table with the given
	 *         identifier exists, otherwise <code>null</code>
	 */
	public LookupTableInfo getTable(String id);

	/**
	 * Remove the lookup table with the given identifier.
	 * 
	 * @param id the resource ID identifying the lookup table to remove
	 * @return <code>true</code> if the lookup table was present and
	 *         successfully removed, <code>false</code> otherwise
	 */
	public boolean removeTable(String id);

	/**
	 * Get the identifiers of the available lookup tables.
	 * 
	 * @return the set of lookup table identifiers
	 */
	public Set<String> getTableIDs();

}
