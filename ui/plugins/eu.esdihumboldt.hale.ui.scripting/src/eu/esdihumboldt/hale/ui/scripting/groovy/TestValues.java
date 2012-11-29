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

package eu.esdihumboldt.hale.ui.scripting.groovy;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Provides test value for use in a script.
 * 
 * @author Simon Templer
 */
public interface TestValues {

	/**
	 * Get a test value for the given property.
	 * 
	 * @param property the property
	 * @return the test value
	 */
	public Object get(PropertyEntityDefinition property);

}
