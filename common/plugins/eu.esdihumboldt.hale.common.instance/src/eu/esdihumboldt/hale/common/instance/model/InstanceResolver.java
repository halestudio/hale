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

import java.util.Collection;

import eu.esdihumboldt.hale.common.instance.model.impl.ReferenceInstanceCollection;

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

	/**
	 * Get an instance collection based on the given instance references.
	 * 
	 * This method allows implementors to optimize retrieval of multiple
	 * references, and to do a lazy resolving.
	 * 
	 * The default implementation delegates to
	 * {@link #getInstance(InstanceReference)} in the iterator.
	 * 
	 * @param references the references to resolve
	 * @return the instances collection based on the references
	 */
	default public InstanceCollection getInstances(
			Collection<? extends InstanceReference> references) {
		return new ReferenceInstanceCollection(references, this);
	}

}
