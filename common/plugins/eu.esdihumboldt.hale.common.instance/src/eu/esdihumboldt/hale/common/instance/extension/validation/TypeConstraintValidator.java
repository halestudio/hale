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
