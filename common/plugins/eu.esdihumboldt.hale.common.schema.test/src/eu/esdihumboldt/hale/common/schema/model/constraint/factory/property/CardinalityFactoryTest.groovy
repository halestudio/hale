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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory.property

import eu.esdihumboldt.hale.common.schema.model.constraint.factory.AbstractPropertiesCompareConstraintFactoryTest
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality


/**
 * Tests for {@link CardinalityFactory}.
 * 
 * @author Simon Templer
 */
class CardinalityFactoryTest extends AbstractPropertiesCompareConstraintFactoryTest<Cardinality> {

	void testOptional() {
		storeRestoreTest(Cardinality.CC_OPTIONAL)
	}

	void testUnbounded() {
		storeRestoreTest(Cardinality.CC_ANY_NUMBER)
	}

	@Override
	protected List<String> getPropertiesToCompare() {
		['minOccurs', 'maxOccurs']
	}
}
