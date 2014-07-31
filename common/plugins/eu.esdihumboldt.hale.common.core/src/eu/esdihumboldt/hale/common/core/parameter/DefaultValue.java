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

package eu.esdihumboldt.hale.common.core.parameter;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Default Value interface for parameter (e.g. provider parameter) Implement
 * this interface to provide a default value and/or sample data for your
 * (provider) parameter type.
 * 
 * @param <T> The complex type
 * 
 * @author Yasmina Kammeyer
 */
public interface DefaultValue {

	/**
	 * @return a default object of the parameter type
	 */
	public Value getDefaultValue();

	/**
	 * Override this method to provide a String representation of the specific
	 * ComplexValue. Used for documentation purposes.
	 * 
	 * @return a String representing an object with sample data
	 */
	public String getSampleData();

}
