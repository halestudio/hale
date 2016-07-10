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

package eu.esdihumboldt.hale.io.html.svg.mapping.json;

import eu.esdihumboldt.hale.common.core.io.Value;
import groovy.json.JsonOutput;

/**
 * Representation of {@link Value}s as Json values.
 * 
 * @author Simon Templer
 */
public interface ValueRepresentation {

	/**
	 * Get the representation of the given value.
	 * 
	 * @param value the value to convert
	 * @return anything {@link JsonOutput}.toJson can handle, e.g. a String
	 */
	Object getValueRepresentation(Value value);

}
