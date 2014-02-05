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

package eu.esdihumboldt.hale.common.schema.model.validate.factory;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.extension.ValidatorFactoryExtension;
import eu.esdihumboldt.util.Pair;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Combines a {@link Value} representation of a {@link Validator} with a type
 * identifier.
 * 
 * @author Simon Templer
 */
public class ValidatorValue {

	/**
	 * The validator type identifier.
	 */
	private final String type;

	/**
	 * The value representing the validator.
	 */
	private final Value value;

	/**
	 * Create a validator representation and type combination.
	 * 
	 * @param type the validator type identifier
	 * @param value the validator representation as value
	 */
	public ValidatorValue(String type, Value value) {
		super();
		this.type = type;
		this.value = value;
	}

	/**
	 * Create a validator representation and type combination from the given
	 * validator.
	 * 
	 * @param validator the validator to represent as value and type
	 * @throws Exception if creating the representation fails or there is no
	 *             associated type identifier
	 */
	public ValidatorValue(Validator validator) throws Exception {
		super();

		Pair<String, Value> pair = ValidatorFactoryExtension.toValue(validator);
		if (pair == null) {
			throw new IllegalStateException("Validator could not be represented as value");
		}
		this.type = pair.getFirst();
		this.value = pair.getSecond();
	}

	/**
	 * Create a validator from the internal validator representation and type.
	 * 
	 * @return the validator created from validator representation and type
	 * @throws Exception if creating the validator fails
	 */
	public Validator toValidator() throws Exception {
		Validator val = ValidatorFactoryExtension.fromValue(value, type);
		if (val == null) {
			throw new IllegalStateException(
					"Unable to restore validator from value for validator type " + type);
		}
		return val;
	}

	/**
	 * @return the value representation of the combination of validator
	 *         representation and type
	 */
	public Value toValue() {
		return Value.complex(this);
	}

	/**
	 * @return the internal validator type identifier
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the internal representation of the validator as value
	 */
	public Value getValidatorRepresentation() {
		return value;
	}

}
