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

import eu.esdihumboldt.hale.common.schema.model.TypeConstraint


/**
 * Base class for {@link ValueConstraintFactory} tests that use simple property
 * comparision to compare original and restored constraints.
 * 
 * @author Simon Templer
 */
abstract class AbstractPropertiesCompareConstraintFactoryTest<T> extends
AbstractValueConstraintFactoryTest<T> {

	protected abstract List<String> getPropertiesToCompare()

	@Override
	protected void compare(T org, T restored) {
		for (String prop in propertiesToCompare) {
			assertEquals "Value for property $prop does not match", org."$prop", restored."$prop"
		}
		// in addition, for type constraints, compare "inheritable"
		if (org instanceof TypeConstraint) {
			assertEquals 'Inheritance of constraint does not match', org.inheritable, restored.inheritable
		}
	}
}
