/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.common.schema.groovy

import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag


/**
 * Test cases for the {@link SchemaBuilder}.
 * 
 * @author Simon Templer
 */
class SchemaBuilderTest extends GroovyTestCase {

	/**
	 * Creates a simple schema and checks if the correct names are assigned to
	 * types and properties.
	 */
	public void testSimpleSchemaNames() {
		def defaultNamespace = 'http://www.example.com'
		def ns1 = 'http://www.perpetummobile.org'
		def ns2 = 'org:standardout:test'
		def empty = ''

		// create the schema
		Schema schema = new SchemaBuilder().schema(defaultNamespace) {
			T1 {
				a()
				b()
			}
			T2(namespace: ns1) {
				c(namespace: empty)
				d(namespace: ns1)
			}
			T3 { e(namespace: ns2) }
		}

		assertEquals "Number of types is incorrect", 3, schema.types.size()

		/*
		 * T1
		 */
		TypeDefinition type1 = schema.types.find {
			it.name.localPart == 'T1' && it.name.namespaceURI == defaultNamespace
		}
		assertNotNull type1

		assertEquals "Number of properties in T1 incorrect", 2, type1.children.size()

		def type1a = type1.children[0]
		assertNotNull type1a
		assertEquals 'a', type1a.name.localPart
		assertEquals defaultNamespace, type1a.name.namespaceURI

		def type1b = type1.children[1]
		assertNotNull type1b
		assertEquals 'b', type1b.name.localPart
		assertEquals defaultNamespace, type1b.name.namespaceURI

		/*
		 * T2
		 */
		TypeDefinition type2 = schema.types.find {
			it.name.localPart == 'T2' && it.name.namespaceURI == ns1
		}
		assertNotNull type2

		assertEquals "Number of properties in T2 incorrect", 2, type2.children.size()

		def type2c = type2.children[0]
		assertNotNull type2c
		assertEquals 'c', type2c.name.localPart
		assertEquals empty, type2c.name.namespaceURI

		def type2d = type2.children[1]
		assertNotNull type2d
		assertEquals 'd', type2d.name.localPart
		assertEquals ns1, type2d.name.namespaceURI

		/*
		 * T3
		 */
		TypeDefinition type3 = schema.types.find {
			it.name.localPart == 'T3' && it.name.namespaceURI == defaultNamespace
		}
		assertNotNull type3

		assertEquals "Number of properties in T3 incorrect", 1, type3.children.size()

		def type3e = type3.children[0]
		assertNotNull type3e
		assertEquals 'e', type3e.name.localPart
		assertEquals ns2, type3e.name.namespaceURI
	}

	/**
	 * Tests if default property types are created correctly.
	 */
	void testDefaultPropertyTypes() {
		String dptns = 'xs'

		SchemaBuilder builder = new SchemaBuilder()
		builder.defaultPropertyTypeNamespace = dptns

		// create the schema
		Schema schema = builder.schema {
			ItemType {
				id(Long)
				name(String)
				price(Double)
				description(String)
			}
		}

		assertEquals "Number of types is incorrect", 1, schema.types.size()

		// retrieve type
		TypeDefinition type = schema.types.find {
			it.name.localPart == 'ItemType' && it.name.namespaceURI == ''
		}
		assertNotNull type

		assertEquals 4, type.children.size()

		// check each property binding
		PropertyDefinition childId = type.children[0]
		assertEquals Long, childId.propertyType.getConstraint(Binding).binding
		assertTrue childId.propertyType.getConstraint(HasValueFlag).enabled

		PropertyDefinition childName = type.children[1]
		assertEquals String, childName.propertyType.getConstraint(Binding).binding
		assertTrue childName.propertyType.getConstraint(HasValueFlag).enabled

		PropertyDefinition childPrice = type.children[2]
		assertEquals Double, childPrice.propertyType.getConstraint(Binding).binding
		assertTrue childPrice.propertyType.getConstraint(HasValueFlag).enabled

		PropertyDefinition childDesc = type.children[3]
		assertEquals String, childDesc.propertyType.getConstraint(Binding).binding
		assertTrue childDesc.propertyType.getConstraint(HasValueFlag).enabled

		// check if name and description types are the same
		assertTrue childName.propertyType == childDesc.propertyType
	}

	/**
	 * Tests if declared types can be referenced as property types in other types.
	 */
	void testDefinedTypeReference() {
		// create the schema
		Schema schema = new SchemaBuilder().schema {
			def itemType = ItemType {
				id(Long)
				name(String)
				price(Double)
				description(String)
			}

			OrderType {
				item(itemType)
				quantity(Integer)
			}
		}

		assertEquals "Number of types is incorrect", 2, schema.types.size()

		// item type
		TypeDefinition itemType = schema.types.find {
			it.name.localPart == 'ItemType'
		}
		assertNotNull itemType

		// order type
		TypeDefinition orderType = schema.types.find {
			it.name.localPart == 'OrderType'
		}
		assertNotNull orderType

		PropertyDefinition childItem = orderType.children[0]
		assertEquals 'item', childItem.name.localPart
		assertTrue itemType == childItem.propertyType
	}

	/**
	 * Test creating a group property definition.
	 */
	void testGroup() {
		// create the schema
		def b = new SchemaBuilder()
		Schema schema = b.schema {
			def itemType = ItemType {
				id(Long)
				name(String)
				price(Double)
				description(String)
			}

			OrderType {
				/*
				 * Strange stuff: A call to the _ method with no parameter
				 * fails in the maven tycho build tests, while it works inside
				 * Eclipse - maybe a Groovy version issue?
				 */
				// _{
				_(cardinality: 1) {
					item(itemType)
					quantity(Integer)
				}
			}
		}

		assertEquals "Number of types is incorrect", 2, schema.types.size()

		// item type
		TypeDefinition itemType = schema.types.find {
			it.name.localPart == 'ItemType'
		}
		assertNotNull itemType

		// order type
		TypeDefinition orderType = schema.types.find {
			it.name.localPart == 'OrderType'
		}
		assertNotNull orderType

		assertEquals 1, orderType.children.size()

		// group
		GroupPropertyDefinition group = orderType.children[0]
		assertNotNull group

		assertEquals 2, group.declaredChildren.size()

		// children
		PropertyDefinition childItem = group.declaredChildren[0]
		assertEquals 'item', childItem.name.localPart
		assertTrue itemType == childItem.propertyType

		PropertyDefinition childQuantity = group.declaredChildren[1]
		assertEquals 'quantity', childQuantity.name.localPart
	}

	/**
	 * Test creating nested default properties.
	 */
	void testNestedDefaultProperties() {
		// create the schema
		Schema schema = new SchemaBuilder().schema {
			OrderType {
				entry {
					item(Long) {
						name()
						price(Double)
						description(String)
					}
					quantity(Integer)
				}
			}
		}

		assertEquals "Number of types is incorrect", 1, schema.types.size()

		// order type
		TypeDefinition orderType = schema.types.find {
			it.name.localPart == 'OrderType'
		}
		assertNotNull orderType

		// entry
		PropertyDefinition entry = orderType.children[0]
		assertNotNull entry
		assertEquals 2, entry.propertyType.children.size()
		// may not have a direct value
		assertFalse entry.propertyType.getConstraint(HasValueFlag).enabled

		// item
		PropertyDefinition item = entry.propertyType.children[0]
		assertNotNull item
		assertEquals 3, item.propertyType.children.size()
		// has a Long value
		assertEquals Long, item.propertyType.getConstraint(Binding).binding
		assertTrue item.propertyType.getConstraint(HasValueFlag).enabled
	}

	/**
	 * Tests creating a type index (instead of a schema).
	 */
	void testCreateTypeIndex() {
		// create the type index
		TypeIndex typeIndex = new SchemaBuilder().types {
			def itemType = ItemType {
				id(Long)
				name(String)
				price(Double)
				description(String)
			}

			OrderType {
				item(itemType)
				quantity(Integer)
			}
		}

		assertNotNull typeIndex
		assertEquals 2, typeIndex.types.size()
	}

	/**
	 * Tests creating a single type definition.
	 */
	void testCreateType() {
		// create the type
		TypeDefinition itemType = new SchemaBuilder().ItemType {
			id(Long)
			name(String)
			price(Double)
			description(String)
		}

		assertNotNull itemType
		assertEquals 4, itemType.children.size()
	}

	/**
	 * Tests creating a single type definition.
	 */
	void testDescriptions() {
		// create the type
		TypeDefinition itemType = new SchemaBuilder().ItemType(description: 'Type') {
			id(Long, description: 'Property')
		}

		assertNotNull itemType
		assertEquals 1, itemType.children.size()

		assertEquals 'Type', itemType.description
		assertEquals 'Property', itemType.children.iterator().next().description
	}

	/**
	 * Test assigning named and custom constraints.
	 */
	void testConstraints() {
		// create the schema
		Schema schema = new SchemaBuilder().schema {
			def itemType = ItemType([
				AbstractFlag.ENABLED,
				new DisplayName('Item')
			]) {
				id(Long, nillable: false, [Cardinality.CC_EXACTLY_ONCE])
				name(String)
				price(Double, [NillableFlag.ENABLED])
				description(String, nillable: true)
			}

			OrderType {
				_ (cardinality: '1..n') {
					item(itemType, cardinality: 1)
					quantity(Integer, cardinality: 1..1)
				}
			}
		}

		assertEquals "Number of types is incorrect", 2, schema.types.size()

		// order type
		TypeDefinition orderType = schema.types.find {
			it.name.localPart == 'OrderType'
		}
		assertNotNull orderType

		assertEquals 1, orderType.children.size()

		// group
		GroupPropertyDefinition group = orderType.children[0]
		assertNotNull group

		assertEquals 2, group.declaredChildren.size()
		assertEquals Cardinality.get(1, Cardinality.UNBOUNDED), group.getConstraint(Cardinality)

		// children
		PropertyDefinition childItem = group.declaredChildren[0]
		assertEquals 'item', childItem.name.localPart
		assertEquals Cardinality.CC_EXACTLY_ONCE, childItem.getConstraint(Cardinality)

		PropertyDefinition childQuantity = group.declaredChildren[1]
		assertEquals 'quantity', childQuantity.name.localPart
		assertEquals Cardinality.CC_EXACTLY_ONCE, childQuantity.getConstraint(Cardinality)

		// item
		TypeDefinition itemType = childItem.propertyType
		assertEquals AbstractFlag.ENABLED, itemType.getConstraint(AbstractFlag)
		assertEquals 'Item', itemType.getConstraint(DisplayName).customName

		assertEquals 4, itemType.children.size()

		// item children
		PropertyDefinition id = itemType.children[0]
		assertEquals 'id', id.name.localPart
		assertEquals NillableFlag.DISABLED, id.getConstraint(NillableFlag)
		assertEquals Cardinality.CC_EXACTLY_ONCE, id.getConstraint(Cardinality)

		PropertyDefinition price = itemType.children[2]
		assertEquals 'price', price.name.localPart
		assertEquals NillableFlag.ENABLED, price.getConstraint(NillableFlag)

		PropertyDefinition desc = itemType.children[3]
		assertEquals 'description', desc.name.localPart
		assertEquals NillableFlag.ENABLED, desc.getConstraint(NillableFlag)
	}

}
