/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.app.transform;

/**
 * Constants for the output to the console.
 * 
 * @author Simon Templer
 */
public interface ConsoleConstants {

	/**
	 * Prefix for messages on System out.
	 */
	public static final String MSG_PREFIX = "(I) ";
	
	/**
	 * Prefix for status messages on System out.
	 */
	public static final String STATUS_PREFIX = "(S) ";
	
	/**
	 * Prefix for warning messages on System out.
	 */
	public static final String WARN_PREFIX = "(W) ";
	
	/**
	 * Prefix for error messages on System out.
	 */
	public static final String ERROR_PREFIX = "(E) ";

}
