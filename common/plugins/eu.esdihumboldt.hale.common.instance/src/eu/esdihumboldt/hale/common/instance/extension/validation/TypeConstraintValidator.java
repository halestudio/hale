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

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Validates type constraints.
 * 
 * @author Kai Schwierczek
 */
public interface TypeConstraintValidator extends ConstraintValidator {

	/**
	 * Validate <code>instance</code> against <code>constraint</code>.
	 * 
	 * @param instance the instance to validate
	 * @param constraint the constraint to validate
	 * @param context the validation context
	 * @throws ValidationException if the validation fails
	 */
	public void validateTypeConstraint(Instance instance, TypeConstraint constraint,
			InstanceValidationContext context) throws ValidationException;
}
