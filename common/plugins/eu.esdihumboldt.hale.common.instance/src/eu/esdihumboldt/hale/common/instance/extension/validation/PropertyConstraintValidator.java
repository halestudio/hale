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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;

/**
 * Validates property constraints.
 * 
 * @author Kai Schwierczek
 */
public interface PropertyConstraintValidator extends ConstraintValidator {

	/**
	 * Validate <code>values</code> against <code>constraint</code>. The
	 * constraint and the values belong to <code>property</code>.
	 * 
	 * @param values the values to validate, may be <code>null</code>
	 * @param constraint the constraint to validate
	 * @param property the property the values and the constraint belong to
	 * @param context the validation context
	 * @param location the validation location
	 * @throws ValidationException if the validation fails
	 */
	public void validatePropertyConstraint(Object[] values, PropertyConstraint constraint,
			PropertyDefinition property, InstanceValidationContext context,
			ValidationLocation location) throws ValidationException;

}
