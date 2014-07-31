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

package eu.esdihumboldt.hale.common.lookup.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.parameter.DefaultValue;
import eu.esdihumboldt.hale.common.lookup.LookupTable;

/**
 * Implementation of the LookupTable Default Value
 * 
 * @author Yasmina Kammeyer
 */
public class LookupTableDefaultValue implements DefaultValue {

	/**
	 * Returns an empty lookup table object.
	 * 
	 * @see eu.esdihumboldt.hale.common.core.parameter.DefaultValue#getDefaultValue()
	 */
	@Override
	public Value getDefaultValue() {
		return Value.of(new LookupTableImpl(new LinkedHashMap<Value, Value>()));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.DefaultValue#getSampleData()
	 */
	@Override
	public String getSampleData() {
		StringBuilder sb = new StringBuilder();
		Map<Value, Value> tmpMap = getDefaultValue().as(LookupTable.class).asMap();
		for (Value key : tmpMap.keySet()) {
			// add-> key , value
			sb.append(key.getStringRepresentation()).append(" , ")
					.append(tmpMap.get(key).getStringRepresentation()).append("\n");
		}

		return sb.toString();
	}

}
