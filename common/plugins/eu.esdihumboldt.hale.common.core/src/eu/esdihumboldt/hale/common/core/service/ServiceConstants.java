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

package eu.esdihumboldt.hale.common.core.service;

/**
 * HALE service related constants.
 * 
 * @author Simon Templer
 */
public interface ServiceConstants {

	/**
	 * The identifier for global scope.
	 * 
	 * FIXME global scope not supported for now (neither in HaleUI nor in
	 * ProjectTransformationEnvironment), see comment in {@link ServiceManager}
	 */
	public static final String SCOPE_GLOBAL = "global";

	/**
	 * The identifier for project scope.
	 */
	public static final String SCOPE_PROJECT = "project";

}
