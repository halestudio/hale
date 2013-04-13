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

package eu.esdihumboldt.hale.ui.util.bbr


/**
 * Bean holding BBR documentation on a type, attribute or value.
 * 
 * @author Simon Templer
 */
class Documentation {

	/**
	 * Alpha numerical code.
	 */
	String code
	
	/**
	 * Three letter short code.
	 */
	String shortCode
	
	/**
	 * Human readable name.
	 */
	String name

	/**
	 * The description retrieved from the BBR.
	 */
	String description

	/**
	 * The definition retrieved from the BBR.
	 */
	String definition
	
	/**
	 * States if the type, attribute or value is marked as in use in the BBR.
	 */
	boolean inUse
	
	/**
	 * States if there is a conflict between the actual use and {@link #inUse},
	 * e.g. a value that is marked as in use but does not appear in the
	 * property type enumeration. 
	 */
	boolean useConflict = false
	
	/**
	 * List of value documentations (optional)
	 */
	List<Documentation> values = []
}
