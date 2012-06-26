package eu.esdihumboldt.hale.common.instancevalidator.validators;

import eu.esdihumboldt.hale.common.instance.extension.validation.GroupPropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;

/**
 * Validator for {@link Cardinality}.
 *
 * @author Kai Schwierczek
 */
public class CardinalityValidator implements GroupPropertyConstraintValidator, PropertyConstraintValidator {
	@Override
	public void validatePropertyConstraint(Object[] values, PropertyConstraint constraint,
			PropertyDefinition property) throws ValidationException {
		validateConstraint(values, (Cardinality) constraint,
				property.getConstraint(NillableFlag.class).isEnabled());
	}

	@Override
	public void validateGroupPropertyConstraint(Object[] values, GroupPropertyConstraint constraint,
			GroupPropertyDefinition property) throws ValidationException {
		validateConstraint(values, (Cardinality) constraint, false);
	}

	private void validateConstraint(Object[] values, Cardinality cardinality,
			boolean nillable) throws ValidationException {
		int count = values == null ? 0 : values.length;
		// 0 is okay in case the property is nillable.
		if (count != 0 || !nillable) {
			if (cardinality.getMinOccurs() > count)
				throw new ValidationException("Not enough values for the property present: " +
						count + " < " + cardinality.getMinOccurs());
			else if (cardinality.getMaxOccurs() != Cardinality.UNBOUNDED && cardinality.getMaxOccurs() < count)
				throw new ValidationException("Too many values for the property present: " +
						count + " > " + cardinality.getMaxOccurs());
		}
	}
}
