/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.model.constraint.factory.type

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.AbstractValueConstraintFactoryTest
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import eu.esdihumboldt.util.validator.AndValidator
import eu.esdihumboldt.util.validator.CombinedValidator
import eu.esdihumboldt.util.validator.DigitCountValidator
import eu.esdihumboldt.util.validator.EnumerationValidator
import eu.esdihumboldt.util.validator.LengthValidator
import eu.esdihumboldt.util.validator.NumberValidator
import eu.esdihumboldt.util.validator.OrValidator
import eu.esdihumboldt.util.validator.PatternValidator
import eu.esdihumboldt.util.validator.Validator


/**
 * Tests for {@link ValidationConstraintFactory}.
 * 
 * @author Simon Templer
 */
class ValidationConstraintFactoryTest extends AbstractValueConstraintFactoryTest<ValidationConstraint> {

	/**
	 * Test w/o validators.
	 */
	void testEmpty() {
		storeRestoreTest(new ValidationConstraint())
	}

	/**
	 * Test w/ a bunch of validators.
	 */
	void testValidators() {
		TypeDefinition type = new DefaultTypeDefinition(new QName('ValidatedType'))
		def typeIndex = [(type): 'someid']

		AndValidator root = new AndValidator([
			new LengthValidator(LengthValidator.Type.MAXIMUM, 14),
			new OrValidator([
				new EnumerationValidator([
					'dumb',
					'clever',
					'interesting',
					'wild'
				]),
				new AndValidator([
					new DigitCountValidator(DigitCountValidator.Type.TOTALDIGITS, 10),
					new NumberValidator(NumberValidator.Type.MAXINCLUSIVE, 10000),
					new NumberValidator(NumberValidator.Type.MININCLUSIVE, 1)
				]),
				new PatternValidator('some pattern')
			])
		])

		ValidationConstraint vc = new ValidationConstraint(root, type)
		storeRestoreTest(vc)
	}

	@Override
	protected void compare(ValidationConstraint org, ValidationConstraint restored) {
		compare(org.validator, restored.validator)
	}

	protected void compare(Validator v1, Validator v2) {
		if (v1 == null && v2 == null) {
			return
		}
		else if (v1 == null || v2 == null) {
			throw new IllegalStateException('One validator is null')
		}

		assertEquals 'Validator types do not match', v1.class, v2.class

		if (v1 instanceof CombinedValidator) {
			// combined validators
			assertEquals 'Number of validators in combined validator does not match',
					v1.validators.size(), v2.validators.size()

			v1.validators.eachWithIndex { def val1, int index ->
				def val2 = v2.validators[index]
				compare(val1, val2)
			}
		}
		else {
			// other validator type
			// just compare description
			assertEquals 'Validator descriptions do not match', v1.description, v2.description
		}
	}
}
