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
