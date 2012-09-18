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

package eu.esdihumboldt.util.validator;

/**
 * Validator for strings.
 * 
 * @author Kai Schwierczek
 */
public interface Validator {

	/**
	 * Checks whether the given value is valid.
	 * 
	 * @param value the value to check
	 * @return null, if the value is valid, otherwise the reason why it's
	 *         invalid
	 */
	public String validate(Object value);

	/**
	 * Returns a human readable representation of this Validator.
	 * 
	 * @return a human readable representation of this Validator
	 */
	public String getDescription();

	/**
	 * Returns true, if this Validator validates all values to true.<br>
	 * The result must not necessarily be correct. If the validator does not
	 * know its result for sure it must return false.
	 * 
	 * @return true, iff the validator knows for sure, that it validates all
	 *         values to true
	 */
	public boolean isAlwaysTrue();
}
