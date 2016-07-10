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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationLocation;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Unique;

/**
 * Validator for {@link Unique}.
 * 
 * @author Kai Schwierczek
 */
public class UniqueValidator implements PropertyConstraintValidator {

	@Override
	public void validatePropertyConstraint(Object[] values, PropertyConstraint constraint,
			PropertyDefinition property, InstanceValidationContext context,
			ValidationLocation location) throws ValidationException {
		Unique unique = (Unique) constraint;
		if (unique.isEnabled() && values != null) {
			for (Object value : values) {
				// only check it if it isn't null
				if (value != null) {
					@SuppressWarnings("unchecked")
					Map<String, Set<Object>> map = (Map<String, Set<Object>>) context
							.getContext(UniqueValidator.class);
					if (map == null) {
						map = new HashMap<String, Set<Object>>();
						context.putContext(UniqueValidator.class, map);
					}
					Set<Object> valueSet = map.get(unique.getIdentifier());
					if (valueSet == null) {
						valueSet = new HashSet<Object>();
						map.put(unique.getIdentifier(), valueSet);
					}
					if (valueSet.contains(value))
						throw new ValidationException("The property " + property.getDisplayName()
								+ " is marked as unique but the value (" + value
								+ ") occurs multiple times.");
					else
						valueSet.add(value);
				}
			}
		}
	}
}
