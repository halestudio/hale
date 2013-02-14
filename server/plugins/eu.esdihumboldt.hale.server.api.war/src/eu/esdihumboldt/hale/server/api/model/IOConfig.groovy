/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.model


/**
 * I/O configuration bean as base for source and target configuration in a
 * transformation.
 * 
 * @author Simon Templer
 */
class IOConfig {

	/**
	 * Constant that is a placeholder for automatic configuration.
	 */
	public static final String AUTO = 'auto'

	/**
	 * The identifier of the I/O provider to use
	 */
	String provider = AUTO

	/**
	 * Name of a configuration preset
	 */
	String preset

	/**
	 * Map of I/O provider settings
	 */
	Map settings
}
