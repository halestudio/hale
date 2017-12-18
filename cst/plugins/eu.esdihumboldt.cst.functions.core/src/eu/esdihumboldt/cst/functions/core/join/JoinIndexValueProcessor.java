/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.core.join;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;

/**
 * Interface for join handler implementations to process property values
 * according to the specific index implementation.
 * 
 * @author Florian Esser
 */
public interface JoinIndexValueProcessor {

	/**
	 * Process a value of a property in a join condition before using it with
	 * the index.
	 * 
	 * @param value the value
	 * @param property the entity definition the value is associated to
	 * @return the processed value, possibly wrapped or replaced through a
	 *         different representation
	 */
	Object processValue(Object value, PropertyEntityDefinition property);
}
