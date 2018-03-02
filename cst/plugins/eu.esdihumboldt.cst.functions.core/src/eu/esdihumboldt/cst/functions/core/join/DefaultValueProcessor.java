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
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexUtil;

/**
 * Standard value processor for Join condition comparisons
 * 
 * @author Florian Esser
 */
public class DefaultValueProcessor implements ValueProcessor {

	/**
	 * Process a value of a property in a join condition
	 * 
	 * @param value the value
	 * @param property the entity definition the value is associated to
	 * @return the processed value, possibly wrapped or replaced through a
	 *         different representation
	 */
	@Override
	public Object processValue(Object value, PropertyEntityDefinition property) {
		return InstanceIndexUtil.processValue(value, property);
	}

}
