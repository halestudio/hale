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

import java.util.Iterator;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.extension.validation.GroupPropertyConstraintValidator;
import eu.esdihumboldt.hale.common.instance.extension.validation.InstanceValidationContext;
import eu.esdihumboldt.hale.common.instance.extension.validation.ValidationException;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;

/**
 * Validator for {@link ChoiceFlag}.
 * 
 * @author Kai Schwierczek
 */
public class ChoiceFlagValidator implements GroupPropertyConstraintValidator {

	@Override
	public void validateGroupPropertyConstraint(Object[] values,
			GroupPropertyConstraint constraint, GroupPropertyDefinition property,
			InstanceValidationContext context) throws ValidationException {
		if (((ChoiceFlag) constraint).isEnabled() && values != null) {
			for (Object value : values) {
				if (value instanceof Group) {
					Iterator<QName> properties = ((Group) value).getPropertyNames().iterator();
					if (!properties.hasNext())
						throw new ValidationException("A choice with no child.");
					properties.next();
					if (properties.hasNext())
						throw new ValidationException("A choice with different children.");
				}
				// XXX what if value is null or not a Group?
				// XXX what if it is an Instance? May it have a value?
			}
		}
	}
}
