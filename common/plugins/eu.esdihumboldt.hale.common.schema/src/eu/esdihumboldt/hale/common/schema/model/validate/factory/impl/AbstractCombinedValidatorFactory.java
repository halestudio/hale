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
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorValue;
import eu.esdihumboldt.util.validator.CombinedValidator;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Base class for {@link Value} factories for {@link CombinedValidator}s.
 * 
 * @author Simon Templer
 * @param <T> the concrete combined validator type
 */
public abstract class AbstractCombinedValidatorFactory<T extends CombinedValidator> implements
		ValidatorFactory<T> {

	@Override
	public Value store(T validator) throws Exception {
		ValueList list = new ValueList();

		for (Validator child : validator.getValidators()) {
			list.add(new ValidatorValue(child).toValue());
		}

		return list.toValue();
	}

	@Override
	public Validator restore(Value value) throws Exception {
		ValueList list = value.as(ValueList.class);

		List<Validator> children = new ArrayList<>();
		for (Value childVal : list) {
			children.add(childVal.as(ValidatorValue.class).toValidator());
		}

		return createValidator(children);
	}

	/**
	 * Create the combined validator based on the given child validators.
	 * 
	 * @param children the child validators
	 * @return the combined validator
	 */
	protected abstract CombinedValidator createValidator(List<Validator> children);

}
