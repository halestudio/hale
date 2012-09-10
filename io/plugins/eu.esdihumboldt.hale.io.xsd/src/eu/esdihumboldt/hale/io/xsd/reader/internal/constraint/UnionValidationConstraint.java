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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import java.util.ArrayList;
import java.util.Collection;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint;
import eu.esdihumboldt.util.validator.OrValidator;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Validation constraint for type unions.
 * 
 * @author Kai Schwierczek
 */
public class UnionValidationConstraint extends ValidationConstraint {

	private Collection<? extends TypeDefinition> unionTypes;
	private boolean initialized = false;
	private Validator validator;

	/**
	 * Create a type union validation constraint.
	 * 
	 * @param unionTypes the definitions of the types contained in the union
	 */
	public UnionValidationConstraint(Collection<? extends TypeDefinition> unionTypes) {
		this.unionTypes = unionTypes;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint#getValidator()
	 */
	@Override
	public Validator getValidator() {
		if (!initialized) {
			ArrayList<Validator> validators = new ArrayList<Validator>(unionTypes.size());
			for (TypeDefinition type : unionTypes)
				validators.add(type.getConstraint(ValidationConstraint.class).getValidator());
			validator = new OrValidator(validators);
		}

		return validator;
	}
}
