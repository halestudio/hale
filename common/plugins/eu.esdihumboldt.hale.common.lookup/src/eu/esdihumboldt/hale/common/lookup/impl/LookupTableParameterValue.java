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
import eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor;

/**
 * Implementation of the LookupTable parameter representation
 * 
 * @author Yasmina Kammeyer
 */
public class LookupTableParameterValue implements ParameterValueDescriptor {

	/**
	 * Returns an empty lookup table object.
	 * 
	 * @see eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor#getDefaultValue()
	 */
	@Override
	public Value getDefaultValue() {
		return Value.of(new LookupTableImpl(new LinkedHashMap<Value, Value>()));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor#getDocumentationRepresentation()
	 */
	@Override
	public String getDocumentationRepresentation() {
		return "The ´key´ has an associated ´value´.";
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.parameter.ParameterValueDescriptor#getSampleData()
	 */
	@Override
	public Value getSampleData() {
		Map<Value, Value> table = new LinkedHashMap<Value, Value>();
		// add some data to the table
		table.put(Value.of("Source_1"), Value.of("Target_1"));

		return Value.of(new LookupTableImpl(table));
	}

}
