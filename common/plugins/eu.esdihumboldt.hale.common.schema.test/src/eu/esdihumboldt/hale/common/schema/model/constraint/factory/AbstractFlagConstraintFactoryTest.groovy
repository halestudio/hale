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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint
import eu.esdihumboldt.hale.common.schema.model.constraint.ConstraintUtil


/**
 * Base class for constraint factories for {@link AbstractFlagConstraint} based
 * constraints.
 * 
 * @author Simon Templer
 * @param <T> the concrete constraint type
 */
abstract class AbstractFlagConstraintFactoryTest<T extends AbstractFlagConstraint> extends
AbstractPropertiesCompareConstraintFactoryTest<T> {

	private final Class<T> concreteType


	AbstractFlagConstraintFactoryTest(Class<T> concreteType) {
		super();
		this.concreteType = concreteType;
	}

	/**
	 * Test with flag enabled.
	 */
	void testEnabled() {
		storeRestoreTest(createConstraint(true))
	}

	/**
	 * Test with flag disabled.
	 */
	void testDisabled() {
		storeRestoreTest(createConstraint(false))
	}

	/**
	 * Test with flag default.
	 */
	void testDefault() {
		AbstractFlagConstraint constraint
		def definition
		try {
			constraint = concreteType.newInstance()
		} catch (e) {
			def constraintType = ConstraintUtil.getConstraintType(concreteType)
			definition = getDefaultConstraintDefinition(constraintType)
			constraint = ConstraintUtil.getDefaultConstraint(constraintType, definition)
		}
		storeRestoreTest(constraint, null, definition)
	}

	protected T createConstraint(boolean enabled) {
		T res
		// try static get method
		try {
			res = concreteType.get(enabled)
		} catch (e) {}

		if (res == null) {
			// try constant
			try {
				if (enabled) {
					res = concreteType.ENABLED
				}
				else {
					res = concreteType.DISABLED
				}
			} catch (e) {}
		}

		if (res == null) {
			// try constant
			try {
				res = concreteType.newInstance(enabled)
			} catch (e) {}
		}

		if (res == null) {
			throw new IllegalStateException('Failed to create flag constraint')
		}

		res
	}

	@Override
	protected List<String> getPropertiesToCompare() {
		return ['enabled']
	}
}
