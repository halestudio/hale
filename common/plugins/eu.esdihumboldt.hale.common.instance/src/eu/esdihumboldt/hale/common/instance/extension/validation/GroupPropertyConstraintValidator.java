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

package eu.esdihumboldt.hale.common.instance.extension.validation;

import eu.esdihumboldt.hale.common.schema.model.GroupPropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;

/**
 * Validates group property constraints.
 *
 * @author Kai Schwierczek
 */
public interface GroupPropertyConstraintValidator extends ConstraintValidator {
	/**
	 * Validate <code>values</code> against <code>constraint</code>.
	 * The constraint and the values belong to <code>property</code>.
	 *
	 * @param values the values to validate, may be <code>null</code>
	 * @param constraint the constraint to validate
	 * @param property the property the values and the constraint belong to
	 * @param context the validation context
	 * @throws ValidationException if the validation fails
	 */
	public void validateGroupPropertyConstraint(Object[] values,
			GroupPropertyConstraint constraint, GroupPropertyDefinition property,
			InstanceValidationContext context) throws ValidationException;
}
