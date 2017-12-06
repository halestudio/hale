/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.index;

import static org.junit.Assert.*;

import javax.xml.namespace.QName;

import org.junit.Test;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Tests for {@link MultimapInstanceIndex}
 * 
 * @author Florian Esser
 */
class MultimapInstanceIndexTest {

	/**
	 * Test that the same mapping cannot be added more than once and removing mapping
	 */
	@Test
	public void testAddRemoveMappingMultipleProperties() {
		MultimapInstanceIndex idx = new MultimapInstanceIndex()

		def typeName = new QName("TestType")
		def type = new DefaultTypeDefinition(typeName)

		def prop1Name = new QName("prop1")
		def prop2Name = new QName("prop2")

		def prop1 = new DefaultPropertyDefinition(prop1Name, type, type)
		def att1 = new ChildContext(prop1)

		def prop2 = new DefaultPropertyDefinition(prop2Name, type, type)
		def att2 = new ChildContext(prop2)

		def ped1 = new PropertyEntityDefinition(type, [att1], SchemaSpaceID.SOURCE, null)
		def ped2 = new PropertyEntityDefinition(type, [att2], SchemaSpaceID.SOURCE, null)

		def mapping1 = new PropertyEntityDefinitionMapping([ped1, ped2].toSet())

		// Mapping of the same entity definitions, but order inverted
		def mapping2 = new PropertyEntityDefinitionMapping([ped2, ped1].toSet())

		// Single property mapping
		def mapping3 = new PropertyEntityDefinitionMapping([ped1].toSet())

		idx.addMapping(mapping1);
		assertEquals(1, idx.getMappings().size())
		assertEquals(mapping1, idx.getMappings().get(0))

		idx.addMapping(mapping2)
		assertEquals(1, idx.getMappings().size())
		assertEquals(mapping1, idx.getMappings().get(0))

		idx.addMapping(mapping3)
		assertEquals(2, idx.getMappings().size())
		assertTrue(idx.getMappings().containsAll([mapping1, mapping3]))

		idx.removeMapping(mapping2)
		assertEquals(1, idx.getMappings().size())
		assertTrue([mapping3].containsAll(idx.getMappings()))
	}
}
