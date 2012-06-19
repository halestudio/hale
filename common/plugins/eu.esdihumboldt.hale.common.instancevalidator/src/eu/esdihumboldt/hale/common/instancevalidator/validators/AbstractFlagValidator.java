package eu.esdihumboldt.hale.common.instancevalidator.validators;

import eu.esdihumboldt.hale.common.instance.extension.validation.TypeConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;

/**
 * Validator for {@link AbstractFlag}.
 *
 * @author Kai Schwierczek
 */
public class AbstractFlagValidator implements TypeConstraintValidator {
	@Override
	public void validateTypeConstraint(Instance instance, TypeConstraint constraint) throws ValidationException {
		if (((AbstractFlag) constraint).isEnabled())
			throw new ValidationException("The type of this instance (" +
					instance.getDefinition().getDisplayName() + ") is abstract.");
	}
}
