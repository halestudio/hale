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

import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationLocation;
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
			PropertyDefinition property, InstanceValidationContext context,
			ValidationLocation location) throws ValidationException {
		if (!((NillableFlag) constraint).isEnabled() && values != null) {
			for (Object value : values)
				if (value == null
						|| (value instanceof Instance
								&& ((Instance) value).getDefinition()
										.getConstraint(HasValueFlag.class).isEnabled()
								&& ((Instance) value).getValue() == null))
					throw new ValidationException("Property that isn't nillable has no content.");
		}
	}
}
