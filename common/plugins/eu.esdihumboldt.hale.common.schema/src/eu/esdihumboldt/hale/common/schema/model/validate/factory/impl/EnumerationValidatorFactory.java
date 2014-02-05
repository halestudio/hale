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

package eu.esdihumboldt.hale.common.schema.model.validate.factory.impl;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorFactory;
import eu.esdihumboldt.util.validator.EnumerationValidator;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Converts {@link EnumerationValidator}s to {@link Value}s and vice versa.
 * 
 * @author Simon Templer
 */
public class EnumerationValidatorFactory implements ValidatorFactory<EnumerationValidator> {

	@Override
	public Value store(EnumerationValidator validator) throws Exception {
		ValueList list = new ValueList();

		for (String value : validator.getValues()) {
			list.add(Value.of(value));
		}

		return list.toValue();
	}

	@Override
	public Validator restore(Value value) throws Exception {
		ValueList list = value.as(ValueList.class);

		List<String> values = new ArrayList<>();
		for (Value val : list) {
			String str = val.as(String.class);
			if (str != null) {
				values.add(str);
			}
			else {
				throw new IllegalStateException("Enumeration value for validator could not be read");
			}
		}

		return new EnumerationValidator(values);
	}

}
