package eu.esdihumboldt.hale.common.instancevalidator.validators;

import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.TypeConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint;

/**
 * Validator for {@link ValidationConstraint}.
 *
 * @author Kai Schwierczek
 */
public class ValidationConstraintValidator implements TypeConstraintValidator {
	// ValidationConstraint includes all information which is included in Enumeration, too.
	// So Enumeration does not need an own validator.

	@Override
	public void validateTypeConstraint(Instance instance, TypeConstraint constraint,
			InstanceValidationContext context) throws ValidationException {
		String result = ((ValidationConstraint) constraint).getValidator().validate(instance.getValue());
		if (result != null)
			throw new ValidationException("Validation of the value (" + instance.getValue() +
					") failed: " + result);
	}
}
