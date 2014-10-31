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

package eu.esdihumboldt.hale.ui.function.internal;

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.groovy.SchemaBuilder
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.ui.selection.SchemaSelection
import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection
import eu.esdihumboldt.hale.ui.selection.impl.DefaultSchemaSelection.SchemaStructuredMode
import eu.esdihumboldt.util.Pair


/**
 * Tests for the {@link AutoCorrelation} class
 * @author Yasmina Kammeyer
 */
class AutoCorrelationTest {

	private static final SUPER_NS = "http://supertype.namespace.com"
	private static final TYPE_NS = "http://supertype.namespace.com/subtype"
	private static final OTHER_NS = "http://completely-different-namespace.com"

	private TypeDefinition superType;
	private TypeDefinition aType;
	private TypeDefinition ansType;
	private TypeDefinition bType;
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
					otherDescription(namespace: OTHER_NS)
					href(URI)
				}
			}

			ansType = A(namespace : OTHER_NS) {
				id(Integer)
				name(String)
				description(String)
				//package/Group -> _
				_(cardinality: 0..1) {
					otherDescription(namespace: OTHER_NS)
					href(URI)
				}
			}

			bType = B(superType: superType) {
				id(OTHER_NS, Integer)
				name(String)
			}

		}
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retype(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 */
	@Test
	public void testRetype() {

		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retype(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 * This method tests the same type selected as source and target.
	 */
	@Test
	public void testRetypeOneType() {
		boolean namespaceIgnore = false;
		Set<TypeDefinition> sourceAndTarget = Collections.emptyList()
		sourceAndTarget.add(aType)

		Set<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = Collections.emptyList()
		AutoCorrelation.createPairsThroughTypeComparison(sourceAndTarget, sourceAndTarget, pairs, namespaceIgnore)
		//There should be one pair
		assertEquals("There should be only one pair!", 1, pairs.size())

		TypeEntityDefinition sourceType = pairs.iterator().next().first
		TypeEntityDefinition targetType = pairs.iterator().next().second

		assertEquals("Source and Target Type should have the same QName", sourceType.getDefinition().getName().toString(), targetType.getDefinition().getName().toString())


		//SchemaSelection selection = new DefaultSchemaSelection(sourceAndTarget, sourceAndTarget, SchemaStructuredMode.ALL);
		//Set<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = AutoCorrelation.retype(selection, namespaceIgnore);
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retype(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 * This method tests the same type selected as source and target.
	 */
	@Test
	public void testRetypeSuperTypeToDifferentNamespaceType() {
		boolean namespaceIgnore = false;
		Set<TypeEntityDefinition> sourceType = new HashSet<TypeEntityDefinition>()
		sourceType.add(new TypeEntityDefinition(superType, SchemaSpaceID.SOURCE, null))
		Set<TypeEntityDefinition> targetType = new HashSet<TypeEntityDefinition>()
		targetType.add(new TypeEntityDefinition(ansType, SchemaSpaceID.TARGET, null))

		SchemaSelection selection = new DefaultSchemaSelection(sourceType, targetType, SchemaStructuredMode.ALL)
		Set<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = AutoCorrelation.retype(selection, namespaceIgnore)
		assertEquals("There should be no pairs, because of different namespaces!", 0, pairs.size())

		namespaceIgnore = true;
		pairs = AutoCorrelation.retype(selection, namespaceIgnore)
		assertEquals("There should be one pair, because namespace will be ignored!", 1, pairs.size())
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retype(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean)}.
	 * This method tests the same type selected as source and target.
	 */
	@Test
	public void testRetypeSuperTypeToType() {
		boolean namespaceIgnore = false;
		Set<TypeEntityDefinition> sourceType = new HashSet<TypeEntityDefinition>()
		sourceType.add(new TypeEntityDefinition(superType, SchemaSpaceID.SOURCE, null))
		Set<TypeEntityDefinition> targetType = new HashSet<TypeEntityDefinition>()
		targetType.add(new TypeEntityDefinition(aType, SchemaSpaceID.TARGET, null))

		SchemaSelection selection = new DefaultSchemaSelection(sourceType, targetType, SchemaStructuredMode.ALL)
		Set<Pair<TypeEntityDefinition, TypeEntityDefinition>> pairs = AutoCorrelation.retype(selection, namespaceIgnore)
		assertEquals("The subType of superType and aType should match (they are the same)!", 1, pairs.size())

	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#rename(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean, boolean)}.
	 */
	@Test
	public void testRename() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link eu.esdihumboldt.hale.ui.function.internal.AutoCorrelation#retypeAndRename(eu.esdihumboldt.hale.ui.selection.SchemaSelection, boolean, boolean)}.
	 */
	@Test
	public void testRetypeAndRename() {
		fail("Not yet implemented"); // TODO
	}
}
