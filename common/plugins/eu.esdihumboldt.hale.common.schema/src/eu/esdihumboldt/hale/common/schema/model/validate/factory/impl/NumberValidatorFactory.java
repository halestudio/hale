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

import java.math.BigDecimal;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueProperties;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorFactory;
import eu.esdihumboldt.util.validator.NumberValidator;
import eu.esdihumboldt.util.validator.NumberValidator.Type;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Converts {@link NumberValidator}s to {@link Value}s and vice versa.
 * 
 * @author Simon Templer
 */
public class NumberValidatorFactory implements ValidatorFactory<NumberValidator> {

	/**
	 * Name of the property holding the length to compare against.
	 */
	private static final String P_VALUE = "value";

	/**
	 * Name of the property holding the mode how to compare the length.
	 */
	private static final String P_TYPE = "compare";

	@Override
	public Value store(NumberValidator validator) throws Exception {
		ValueProperties props = new ValueProperties();

		props.put(P_TYPE, Value.of(validator.getType().name()));
		props.put(P_VALUE, Value.of(validator.getValue()));

		return props.toValue();
	}

	@Override
	public Validator restore(Value value) throws Exception {
		ValueProperties props = value.as(ValueProperties.class);

		Type type = Type.valueOf(props.getSafe(P_TYPE).as(String.class));
		BigDecimal val = props.getSafe(P_VALUE).as(BigDecimal.class);

		return new NumberValidator(type, val);
	}

}
