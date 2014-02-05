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

package eu.esdihumboldt.hale.common.schema.model.constraint.type.factory;

import java.util.Map;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ClassResolver;
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.ValueConstraintFactory;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint;
import eu.esdihumboldt.hale.common.schema.model.validate.factory.ValidatorValue;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Converts {@link ValidationConstraint}s to {@link Value}s and vice versa.
 * 
 * @author Simon Templer
 */
public class ValidationConstraintFactory implements ValueConstraintFactory<ValidationConstraint> {

	@Override
	public Value store(ValidationConstraint constraint, Map<TypeDefinition, String> typeIndex)
			throws Exception {
		return new ValidatorValue(constraint.getValidator()).toValue();
	}

	@Override
	public ValidationConstraint restore(Value value, Definition<?> definition,
			Map<String, TypeDefinition> typeIndex, ClassResolver resolver) throws Exception {
		Validator validator = value.as(ValidatorValue.class).toValidator();
		TypeDefinition type = (TypeDefinition) definition;
		return new ValidationConstraint(validator, type);
	}

}
