/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.instancevalidator.validators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.PropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Unique;

/**
 * Validator for {@link Unique}.
 *
 * @author Kai Schwierczek
 */
public class UniqueValidator implements PropertyConstraintValidator {
	/**
	 * @see PropertyConstraintValidator#validatePropertyConstraint(Object[], PropertyConstraint, PropertyDefinition, InstanceValidationContext)
	 */
	@Override
	public void validatePropertyConstraint(Object[] values, PropertyConstraint constraint, PropertyDefinition property,
			InstanceValidationContext context) throws ValidationException {
		Unique unique = (Unique) constraint;
		if (unique.isEnabled() && values != null) {
			for (Object value : values) {
				// only check it if it isn't null
				if (value != null) {
					@SuppressWarnings("unchecked")
					Map<String, Set<Object>> map = (Map<String, Set<Object>>) context.getContext(UniqueValidator.class);
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
						throw new ValidationException("The property " + 
								property.getDisplayName() + " is marked as unique but the value (" +
								value + ") occurs multiple times.");
					else
						valueSet.add(value);
				}
			}
		}
	}
}
