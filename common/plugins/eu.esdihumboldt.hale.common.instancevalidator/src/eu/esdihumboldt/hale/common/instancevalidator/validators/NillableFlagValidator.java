package eu.esdihumboldt.hale.common.instancevalidator.validators;

import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Validator for {@link NillableFlag}.
 * 
 * @author Kai Schwierczek
 */
public class NillableFlagValidator implements PropertyConstraintValidator {

	@Override
	public void validatePropertyConstraint(Object[] values, PropertyConstraint constraint,
			PropertyDefinition property, InstanceValidationContext context)
			throws ValidationException {
		if (!((NillableFlag) constraint).isEnabled() && values != null) {
			for (Object value : values)
				if (value == null
						|| (value instanceof Instance
								&& ((Instance) value).getDefinition()
										.getConstraint(HasValueFlag.class).isEnabled() && ((Instance) value)
								.getValue() == null))
					throw new ValidationException("Property that isn't nillable has no content.");
		}
	}
}
