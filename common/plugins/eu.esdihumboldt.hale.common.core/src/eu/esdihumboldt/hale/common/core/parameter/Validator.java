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

package eu.esdihumboldt.hale.common.core.parameter;

import com.google.common.collect.ListMultimap;

/**
 * Validator for function parameters.
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
	public String validate(String value);

	/**
	 * Sets this validators parameters.
	 * 
	 * @param parameters the parameters
	 */
	public void setParameters(ListMultimap<String, String> parameters);
}
