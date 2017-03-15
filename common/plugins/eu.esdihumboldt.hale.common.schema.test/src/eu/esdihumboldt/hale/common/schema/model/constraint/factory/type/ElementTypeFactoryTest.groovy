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

import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.factory.AbstractPropertiesCompareConstraintFactoryTest
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition


/**
 * Tests for {@link ElementTypeFactory}.
 * 
 * @author Simon Templer
 */
class ElementTypeFactoryTest extends AbstractPropertiesCompareConstraintFactoryTest<ElementType> {

	/**
	 * Default - no binding.
	 */
	void testNoBinding() {
		storeRestoreTest(new ElementType())
	}

	/**
	 * A binding but not an associated type.
	 */
	void testBinding() {
		storeRestoreTest(ElementType.get(String))
	}

	/**
	 * A binding and a known binding type definition.
	 */
	void testTypeBinding() {
		TypeDefinition type = new DefaultTypeDefinition(new QName('BindingType'))

		def typeIndex = [(type): 'someid' as Value]

		storeRestoreTest(ElementType.createFromType(type), typeIndex, null)
	}

	@Override
	protected List<String> getPropertiesToCompare() {
		['binding', 'definition']
	}
}
