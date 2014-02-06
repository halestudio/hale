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

import eu.esdihumboldt.hale.common.schema.model.constraint.factory.AbstractPropertiesCompareConstraintFactoryTest
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration


/**
 * Tests for {@link EnumerationFactory}.
 * 
 * @author Simon Templer
 */
class EnumerationFactoryTest extends AbstractPropertiesCompareConstraintFactoryTest<Enumeration> {

	void testEmpty() {
		storeRestoreTest(new Enumeration())
	}

	void testValues() {
		storeRestoreTest(new Enumeration([
			'value1',
			'some value',
			'val2'
		], false))
	}

	void testValuesAllowOthers() {
		storeRestoreTest(new Enumeration([
			'value1',
			'some value',
			'val2'
		], true))
	}

	@Override
	protected List<String> getPropertiesToCompare() {
		['values', 'allowOthers']
	}
}
