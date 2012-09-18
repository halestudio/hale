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
