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
 * Interface for instance resolvers, that allow getting a reference for an
 * instance and vice versa.
 * 
 * @author Simon Templer
 */
public interface InstanceResolver {

	/**
	 * Get a reference to an instance that can be used to retrieve the given
	 * instance using {@link #getInstance(InstanceReference)}.
	 * 
	 * @param instance the instance, must have originated from this resolver
	 * @return the reference
	 */
	public InstanceReference getReference(Instance instance);

	/**
	 * Get the instance referenced by the given reference.
	 * 
	 * @param reference the instance reference
	 * @return the referenced instance or <code>null</code> if it does not exist
	 *         or the reference is invalid
	 */
	public Instance getInstance(InstanceReference reference);

	/*
	 * TODO add method to get instances for multiple references? would allow to
	 * optimize retrieval e.g. for GmlInstanceCollection
	 * 
	 * best would be a list with preserved order, as this would allow
	 * determining which instance belongs to which reference, as such allowing
	 * dereferencing instances for a larger number of references, e.g. for
	 * several instance sets/partitions
	 */

}
