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

import eu.esdihumboldt.hale.common.schema.model.constraint.factory.AbstractPropertiesCompareConstraintFactoryTest
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey


/**
 * Tests for {@link PrimaryKeyFactory}.
 * 
 * @author Simon Templer
 */
class PrimaryKeyFactoryTest extends AbstractPropertiesCompareConstraintFactoryTest<PrimaryKey> {

	void testDefault() {
		storeRestoreTest(new PrimaryKey())
	}

	void testPath1() {
		storeRestoreTest(new PrimaryKey([new QName('test')]))
	}

	void testPath2() {
		storeRestoreTest(new PrimaryKey([
			new QName('test'),
			new QName('ns', 'nm')
		]))
	}

	@Override
	protected List<String> getPropertiesToCompare() {
		['primaryKeyPath']
	}
}
