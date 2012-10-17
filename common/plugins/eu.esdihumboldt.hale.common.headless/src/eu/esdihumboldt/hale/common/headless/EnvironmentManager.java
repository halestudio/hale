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

/**
 * Manages the set of active {@link TransformationEnvironment}s.
 * 
 * @author Simon Templer
 */
public interface EnvironmentManager extends EnvironmentService {

	/**
	 * Add a transformation environment.
	 * 
	 * @param environment the environment to add
	 */
	public void addEnvironment(TransformationEnvironment environment);

	/**
	 * Remove a transformation environment.
	 * 
	 * @param id the identifier of the environment to remove
	 */
	public void removeEnvironment(String id);

}
