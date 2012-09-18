/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.model;

/**
 * Filter for {@link Instance}s. Filter implementations should reimplement
 * {@link #equals(Object)} and {@link #hashCode()}.
 * 
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public interface Filter {

	/**
	 * Determines if an instance matches the filter.
	 * 
	 * @param instance the instance to check the filter against
	 * @return <code>true</code> if the given instance matches the filter,
	 *         <code>false</code> otherwise
	 */
	public boolean match(Instance instance);

	/*
	 * XXX it might be a good option to include the information about valid
	 * instance types in the filter interface, as this would allow an easier
	 * optimization for filtering instance collections based on types!
	 */
//	public Set<TypeDefinition> getAllowedTypes();

}
