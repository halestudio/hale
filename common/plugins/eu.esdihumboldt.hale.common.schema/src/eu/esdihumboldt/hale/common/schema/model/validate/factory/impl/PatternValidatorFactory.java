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

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorFactory;
import eu.esdihumboldt.util.validator.PatternValidator;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Converts a {@link PatternValidator} to a {@link Value} and vice versa.
 * 
 * @author Simon Templer
 */
public class PatternValidatorFactory implements ValidatorFactory<PatternValidator> {

	@Override
	public Value store(PatternValidator validator) {
		return Value.of(validator.getPattern());
	}

	@Override
	public Validator restore(Value value) throws Exception {
		return new PatternValidator(value.as(String.class));
	}

}
