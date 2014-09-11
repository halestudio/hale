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

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * {@link ParameterValueDescriptor} interface for parameter (e.g. provider
 * parameter). Implement this interface to provide a default value and/or sample
 * data for your parameter type.
 * 
 * @author Yasmina Kammeyer
 */
public interface ParameterValueDescriptor {

	/**
	 * @return a default object of the parameter type or <code>null</code> if no
	 *         default value is provided
	 */
	@Nullable
	public Value getDefaultValue();

	/**
	 * @return the sample data of the parameter or <code>null</code> if no
	 *         sample value is provided
	 */
	@Nullable
	public Value getSampleData();

	/**
	 * Override this method to provide a String representation of the specific
	 * Parameter. Used for documentation purposes.
	 * 
	 * @return a String representing an object with sample data or null
	 */
	@Nullable
	public String getDocumentationRepresentation();

}
