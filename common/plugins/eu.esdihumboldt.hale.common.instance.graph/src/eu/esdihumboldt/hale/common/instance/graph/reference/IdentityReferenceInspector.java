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

package eu.esdihumboldt.hale.common.instance.graph.reference;

import java.util.Set;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Determines identities and references/assocations for an instance.
 * 
 * @author Simon Templer
 * @param <T> the identifier type, must have a sensible equals implementation
 */
public interface IdentityReferenceInspector<T> {

	/**
	 * Get the identity the instance can be identified with.
	 * 
	 * @param instance the instance to identify
	 * @return the instance's identifier or <code>null</code>
	 */
	@Nullable
	T getIdentity(Instance instance);

	/**
	 * Get the identities the instance can be identified with.
	 * 
	 * @param instance the instance to identify
	 * @return the instance's identifiers
	 */
	Set<T> getReferencedIdentities(Instance instance);

}
