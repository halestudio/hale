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

package eu.esdihumboldt.hale.common.core.io;

/**
 * Interface for complex values supporting a string representation for backwards
 * compatibility. Implementors must have a default constructor.
 * 
 * @author Simon Templer
 */
public interface LegacyComplexValue {

	/**
	 * Load the object from a string representation.
	 * 
	 * @param value the object's string representation
	 */
	public void loadFromString(String value);

}
