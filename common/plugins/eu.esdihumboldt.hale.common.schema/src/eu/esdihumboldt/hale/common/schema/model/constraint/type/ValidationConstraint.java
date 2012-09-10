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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import java.util.ArrayList;
import java.util.Collections;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.validator.AndValidator;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Constraint holding information about validation rules specified by
 * XMLSchemaFacets.
 * 
 * @author Kai Schwierczek
 */
@Constraint(mutable = false)
public class ValidationConstraint implements TypeConstraint {

	private Validator validator;
	private final TypeDefinition type;
	private boolean init = false;

	/**
	 * Default constructor.
	 */
	public ValidationConstraint() {
		validator = new AndValidator(Collections.<Validator> emptyList());
		type = null;
	}

	/**
	 * Constructs a constraint with the given validator.
	 * 
	 * @param validator the validator
	 * @param type the type
	 */
	public ValidationConstraint(Validator validator, TypeDefinition type) {
		this.validator = validator;
		this.type = type;
	}

	/**
	 * Returns the validator for this constraint.
	 * 
	 * @return the validator for this constraint
	 */
	public Validator getValidator() {
		if (type != null && type.getSuperType() != null && !init) {
			init = true;
			Validator parentValidator = type.getSuperType()
					.getConstraint(ValidationConstraint.class).getValidator();
			if (!parentValidator.isAlwaysTrue()) {
				ArrayList<Validator> validators = new ArrayList<Validator>(2);
				validators.add(parentValidator);
				validators.add(validator);
				validator = new AndValidator(validators);
			}
		}
		return validator;
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// inherit unless overridden
		return true;
	}
}
