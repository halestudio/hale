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

package eu.esdihumboldt.hale.common.autocorrelation.test;

import org.junit.Before
import org.junit.Test

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.autocorrelation.AutoCorrelation
import eu.esdihumboldt.hale.common.autocorrelation.AutoCorrelationComparatorImpl
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.util.Pair


/**
 * Tests for the {@link AutoCorrelation} class
 * @author Yasmina Kammeyer
 */
class AutoCorrelationTest extends GroovyTestCase{

	private static final SUPER_NS = "http://supertype.namespace.com"
	private static final TYPE_NS = "http://supertype.namespace.com/subtype"
	private static final OTHER_NS = "http://completely-different-namespace.com"

	private TypeDefinition superType;
	private TypeDefinition aType;
	private TypeDefinition ansType;
	private TypeDefinition bType;
	private AutoCorrelationComparatorImpl comparator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		new SchemaBuilder(defaultPropertyTypeNamespace: TYPE_NS).schema(SUPER_NS) {

			superType = Super(namespace : SUPER_NS){ inheritedProperty(SUPER_NS, String) }

			aType = A(namespace : TYPE_NS, superType : superType) {
				id(Integer)
				name(String)
				description(String)
				//package/Group -> _
				_(cardinality: 0..1) {
					otherDescription(namespace: TYPE_NS)
					href(URI)
				}
			}

			ansType = A(namespace : OTHER_NS) {
				id(Integer)
				inheritedProperty(String)
				name(String)
				description(String)
				//package/Group -> _
				_(cardinality: 0..1) {
					otherDescription(namespace: OTHER_NS)
					href(URI)
				}
			}

			bType = B(superType: superType) {
				id(namespace : OTHER_NS, Integer)
				name(String)
			}

		}

		comparator = new AutoCorrelationComparatorImpl()
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#rename(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 */
	@Test
	public void ignore_testStructuralRename() {
		// This method should test the appliance of structural Rename
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retype(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 * This method tests the same type selected as source and target.
	 */
	@Test
	public void testRetypeSameType() {
		boolean namespaceIgnore = false;
		Collection<TypeDefinition> sourceAndTarget = new ArrayList<TypeDefinition>()
		sourceAndTarget.add(aType)

		Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = new ArrayList<Pair<TypeEntityDefinition, TypeEntityDefinition>>()
		AutoCorrelation.createPairsThroughTypeComparison(sourceAndTarget, sourceAndTarget, pairs, namespaceIgnore, comparator)
		//There should be one pair
		assertEquals("There should be only one pair!", 1, pairs.size())

		assertEquals("Source and Target Type should have the same QName", pairs[0].first.getDefinition().getName().toString(), pairs[0].second.getDefinition().getName().toString())
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retype(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 * This method tests the same type selected as source and target.
	 */
	@Test
	public void testRetypeSuperTypeToDifferentNamespaceType() {
		boolean namespaceIgnore = false;

		Collection<TypeDefinition> sourceTypes = [superType]
		Collection<TypeDefinition> targetTypes = [ansType]

		Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = AutoCorrelation.retype(sourceTypes, targetTypes, namespaceIgnore, comparator)
		assertEquals("There should be no pairs, because of different namespaces!", 0, pairs.size())

		namespaceIgnore = true;
		pairs = AutoCorrelation.retype(sourceTypes, targetTypes, namespaceIgnore, comparator)
		assertEquals("There should be one pair, because namespace will be ignored!", 1, pairs.size())
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retype(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 * This method tests the same type selected as source and target.
	 */
	@Test
	public void testRetypeSuperTypeToType() {
		boolean namespaceIgnore = false;

		Collection<TypeDefinition> sourceTypes = [superType]
		Collection<TypeDefinition> targetTypes = [aType]

		Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = AutoCorrelation.retype(sourceTypes, targetTypes, namespaceIgnore, comparator)
		assertEquals("The subType of superType and aType should match (they are the same)!", 1, pairs.size())
		assertEquals("Source and Target Type should have the same QName", pairs[0].first.getDefinition().getName().toString(), pairs[0].second.getDefinition().getName().toString())
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#rename(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean, boolean)}.
	 */
	@Test
	public void testRename() {

		TypeEntityDefinition source = new TypeEntityDefinition(aType, SchemaSpaceID.SOURCE, null)
		TypeEntityDefinition target = new TypeEntityDefinition(aType, SchemaSpaceID.TARGET, null)

		Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = [
			new Pair<TypeEntityDefinition, TypeEntityDefinition>(source, target)
		]

		Map<Pair<TypeEntityDefinition, TypeEntityDefinition>, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> propertyPairs = new HashMap()

		propertyPairs = AutoCorrelation.rename(pairs, 1, false, false, true, comparator)

		assertEquals("There have to be 4 properties with one iteration.", 4, propertyPairs.values()[0].size())

		for(Pair<PropertyEntityDefinition, PropertyEntityDefinition> prop : propertyPairs.values()[0]){

			assertTrue "The property 'otherDescription' should not be included, at this point.", !prop.getFirst().getDefinition().getName().getLocalPart().equals("otherDescription")

		}

		propertyPairs = AutoCorrelation.rename(pairs, 4, false, false, true, comparator)

		assertEquals("There have to be 6 properties with two iterations.", 6, propertyPairs.values()[0].size())

	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#rename(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean, boolean)}.
	 */
	@Test
	public void testRenameNamespace() {
		TypeEntityDefinition source = new TypeEntityDefinition(aType, SchemaSpaceID.SOURCE, null)
		TypeEntityDefinition target = new TypeEntityDefinition(bType, SchemaSpaceID.TARGET, null)

		Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = [
			new Pair<TypeEntityDefinition, TypeEntityDefinition>(source, target)
		]

		Map<Pair<TypeEntityDefinition, TypeEntityDefinition>, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> propertyPairs = new HashMap()

		propertyPairs = AutoCorrelation.rename(pairs, 3, false, false, true, comparator)

		assertEquals "The property 'id' has different namespaces, so only the name and inherited property should be found as a pair.", 2, propertyPairs.values()[0].size()
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#rename(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean, boolean)}.
	 */
	@Test
	public void testRenameUseSuperType() {
		TypeEntityDefinition source = new TypeEntityDefinition(aType, SchemaSpaceID.SOURCE, null)
		TypeEntityDefinition target = new TypeEntityDefinition(ansType, SchemaSpaceID.TARGET, null)

		Collection<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = [
			new Pair<TypeEntityDefinition, TypeEntityDefinition>(source, target)
		]

		Map<Pair<TypeEntityDefinition, TypeEntityDefinition>, Collection<Pair<PropertyEntityDefinition, PropertyEntityDefinition>>> propertyPairs = new HashMap()

		propertyPairs = AutoCorrelation.rename(pairs, 3, true, true, true, comparator)


		for(Pair<PropertyEntityDefinition, PropertyEntityDefinition> prop : propertyPairs.values()[0]){

			if(prop.getFirst().getType().equals(superType))
				assertEquals "The inheritedProperty (source property) should be mapped to its superType", superType.getChildren()[0].getPropertyType(), prop.getFirst().getDefinition().getPropertyType()
		}

	}

}
