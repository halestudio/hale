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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.headless;

import java.util.Collection;

/**
 * Holds active {@link TransformationEnvironment}s.
 * 
 * @author Simon Templer
 */
public interface EnvironmentService {

	/**
	 * Get the available transformation environments.
	 * 
	 * @return an unmodifiable collection of transformation environments
	 */
	public Collection<TransformationEnvironment> getEnvironments();

	/**
	 * Get the transformation environment with the given identifier, if it
	 * exists.
	 * 
	 * @param id the identifier
	 * @return the environment matching the identifier or <code>null</code> if
	 *         there is none
	 */
	public TransformationEnvironment getEnvironment(String id);

	// TODO support listeners?

}
