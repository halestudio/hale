/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */
package eu.esdihumboldt.hale.common.instancevalidator.validators;

import eu.esdihumboldt.hale.common.instance.extension.validation.GroupPropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationLocation;
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
public class CardinalityValidator
		implements GroupPropertyConstraintValidator, PropertyConstraintValidator {

	@Override
	public void validatePropertyConstraint(Object[] values, PropertyConstraint constraint,
			PropertyDefinition property, InstanceValidationContext context,
			ValidationLocation location) throws ValidationException {
		validateConstraint(values, (Cardinality) constraint,
				property.getConstraint(NillableFlag.class).isEnabled());
	}

	@Override
	public void validateGroupPropertyConstraint(Object[] values, GroupPropertyConstraint constraint,
			GroupPropertyDefinition property, InstanceValidationContext context)
					throws ValidationException {
		validateConstraint(values, (Cardinality) constraint, false);
	}

	private void validateConstraint(Object[] values, Cardinality cardinality, boolean nillable)
			throws ValidationException {
		int count = values == null ? 0 : values.length;
		// 0 is okay in case the property is nillable.
		if (count != 0 || !nillable) {
			if (cardinality.getMinOccurs() > count)
				throw new ValidationException("Not enough values for the property present: " + count
						+ " < " + cardinality.getMinOccurs());
			else if (cardinality.getMaxOccurs() != Cardinality.UNBOUNDED
					&& cardinality.getMaxOccurs() < count)
				throw new ValidationException("Too many values for the property present: " + count
						+ " > " + cardinality.getMaxOccurs());
		}
	}
}
