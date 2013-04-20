/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.propagate.config;

import java.util.Set;

/**
 * Feature mapping configuration interface.
 * 
 * @author Simon Templer
 */
public interface FeatureMap {

	/**
	 * Get the possible source types for the given target type.
	 * 
	 * @param targetTypeName the target type local name
	 * @return the local names of the possible source types
	 */
	public Set<String> getPossibleSourceTypes(String targetTypeName);

}
