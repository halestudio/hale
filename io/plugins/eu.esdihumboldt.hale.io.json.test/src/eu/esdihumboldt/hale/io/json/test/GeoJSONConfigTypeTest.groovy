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

package eu.esdihumboldt.hale.io.json.test;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.json.GeoJSONConfig
import eu.esdihumboldt.util.xml.XmlUtil


/**
 * TODO Type description
 * @author Simon Templer 
 */
class GeoJSONConfigTypeTest {

	private static final String MAIN_NS = 'http://www.example.com'

	private static final String PROPERTY_NS = 'http://www.example.com/property'

	private static final String SPECIAL_NS = 'totally-different-namespace'

	private TypeDefinition itemType;

	private TypeDefinition orderType;

	@Before
	public void createTestSchema() throws Exception {
		new SchemaBuilder(defaultPropertyTypeNamespace: PROPERTY_NS).schema(MAIN_NS) {
			itemType = Item {
				name()
				price(Double)
				alternateNames { name(cardinality: '*') }
				_(cardinality: 0..1) {
					name(namespace: SPECIAL_NS)
					link(URI)
				}
			}

			orderType = SingleOrder {
				item(itemType)
				quantity(Integer)
			}
		}
	}

	@Test
	public void testReadWrite() {
		GeoJSONConfig config = new GeoJSONConfig()

		TypeDefinition type1 = itemType
		TypeEntityDefinition typeEntity1 = new TypeEntityDefinition(type1, SchemaSpaceID.TARGET, null)
		PropertyEntityDefinition property1 = typeEntity1.accessor().price as PropertyEntityDefinition
		config.addDefaultGeometry(type1, property1)

		TypeDefinition type2 = orderType
		TypeEntityDefinition typeEntity2 = new TypeEntityDefinition(type2, SchemaSpaceID.TARGET, null)
		PropertyEntityDefinition property2 = typeEntity2.accessor().quantity as PropertyEntityDefinition
		config.addDefaultGeometry(type2, property2)

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(config)

		println XmlUtil.serialize(fragment, true)

		// convert back
		GeoJSONConfig conv = HaleIO.getComplexValue(fragment, GeoJSONConfig, null)

		assertNotNull conv

		PropertyEntityDefinition convProperty1 = conv.getDefaultGeometry(type1)
		assertNotNull convProperty1
		assertEquals convProperty1, property1

		PropertyEntityDefinition convProperty2 = conv.getDefaultGeometry(type2)
		assertNotNull convProperty2
		assertEquals convProperty2, property2
	}
}
