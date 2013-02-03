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

/**
 * Lookup table and meta information.
 * 
 * @param <K> the lookup key type
 * @param <V> the type of values retrieved from the table
 * @author Simon Templer
 */
public interface LookupTableInfo<K, V> {

	/**
	 * Get the lookup table human readable name.
	 * 
	 * @return the lookup table name
	 */
	public String getName();

	/**
	 * Get the optional lookup table description.
	 * 
	 * @return the description or <code>null</code>
	 */
	public String getDescription();

	/**
	 * Get the lookup table.
	 * 
	 * @return the lookup table
	 */
	public LookupTable<K, V> getTable();

}
