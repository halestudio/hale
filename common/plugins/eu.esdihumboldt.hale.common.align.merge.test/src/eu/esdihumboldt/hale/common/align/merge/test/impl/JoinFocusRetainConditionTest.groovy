/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.merge.test.impl

import static org.junit.Assert.*

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO
import eu.esdihumboldt.hale.common.align.merge.MergeSettings
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter

/**
 * Test for retaining conditions during Merge.
 * 
 * Note: When running locally in Eclipse as JUnit Plugin-Test,
 * and you get java.lang.NoClassDefFoundError: org/w3c/dom/ElementTraversal,
 * exclude the org.apache.xerces bundle from the plugins.
 * 
 * @author Simon Templer
 */
class JoinFocusRetainConditionTest extends AbstractMergeCellMigratorTest {

	@BeforeClass
	static void setup() {
		MergeSettings.setTransferContextsToJoinFocus(true)
	}

	@AfterClass
	static void reset() {
		MergeSettings.setTransferContextsToJoinFocus(false)
	}

	@Test
	void testJoinCondition() {
		def toMigrate = this.class.getResource('/testcases/retain-condition-join/B-to-C.halex')
		def cellId = 'B1toC1'

		def matching = this.class.getResource('/testcases/retain-condition-join/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks

		// filter
		assertEquals(1, migrated.size())
		JaxbAlignmentIO.printCell(migrated[0], System.out)

		assertNotNull(migrated[0].source)
		assertEquals(2, migrated[0].source.size())
		Collection<? extends Entity> source = migrated[0].source.values()
		((Collection<Entity>) source).each { e ->
			def filter = e.definition.filter
			if (e.definition.definition.displayName == 'A1') {
				// expect filter to have been propagated to A1
				assertNotNull(filter)
				//assertEquals('a1 <> \'NIL\'', filter.filterTerm)
				assertEquals('NOT ("a1" = \'NIL\')', filter.filterTerm)
			}
			else {
				// no filter should be present
				assertNull(filter)
			}
		}

		JoinParameter param = CellUtil.getFirstParameter(migrated[0], JoinFunction.PARAMETER_JOIN).as(JoinParameter)
		assertJoinOrder(param, ['A1', 'A2'])

		// there should be a condition on the join focus, also in the order
		assertNotNull(param.types[0].filter)

		// there should also be a filter in the condition
		def base = param.conditions.collect { it.baseProperty }.findAll { it.type.displayName == 'A1' }.toList()
		assertEquals(1, base.size())
		assertNotNull(base[0].filter)

		// there should be a message about the condition having been translated automatically
		def messages = getMigrationMessages(migrated[0])
		assertTrue(messages.any { msg ->
			msg.text.toLowerCase().contains('condition')
		})
	}

	@Test
	void testJoinsCondition() {
		def toMigrate = this.class.getResource('/testcases/retain-condition-join/B-to-C.halex')
		def cellId = 'B3B5toC3'

		def matching = this.class.getResource('/testcases/retain-condition-join/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks

		// filter
		assertEquals(1, migrated.size())
		JaxbAlignmentIO.printCell(migrated[0], System.out)

		assertNotNull(migrated[0].source)
		assertEquals(4, migrated[0].source.size())
		Collection<? extends Entity> source = migrated[0].source.values()
		((Collection<Entity>) source).each { e ->
			def filter = e.definition.filter
			if (e.definition.definition.displayName == 'A3') {
				// expect filter to have been propagated to A3
				assertNotNull(filter)
				assertEquals('"a3" > 10', filter.filterTerm)
			}
			else {
				// no filter should be present
				assertNull(filter)
			}
		}

		JoinParameter param = CellUtil.getFirstParameter(migrated[0], JoinFunction.PARAMETER_JOIN).as(JoinParameter)
		assertJoinOrder(param, ['A3', 'A4', 'A5', 'A6'])

		// there should be a condition on the join focus, also in the order
		assertNotNull(param.types[0].filter)

		// there should also be a filter in the condition
		def base = param.conditions.collect { it.baseProperty }.findAll { it.type.displayName == 'A3' }.toList()
		assertEquals(2, base.size())
		assertNotNull(base[0].filter)
		assertNotNull(base[1].filter)

		// there should be a message about the condition having been translated automatically
		def messages = getMigrationMessages(migrated[0])
		assertTrue(messages.any { msg ->
			msg.text.toLowerCase().contains('condition')
		})
	}

	@Test
	void testGroovyRetypeCondition() {
		def toMigrate = this.class.getResource('/testcases/retain-condition-join/B-to-C.halex')
		def cellId = 'B6toC4'

		def matching = this.class.getResource('/testcases/retain-condition-join/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks

		// filter
		assertEquals(1, migrated.size())
		JaxbAlignmentIO.printCell(migrated[0], System.out)

		assertNotNull(migrated[0].source)
		assertEquals(2, migrated[0].source.size())
		Collection<? extends Entity> source = migrated[0].source.values()
		((Collection<Entity>) source).each { e ->
			def filter = e.definition.filter
			if (e.definition.definition.displayName == 'A7') {
				// expect filter to have been propagated to A7
				assertNotNull(filter)
				assertEquals('NOT ("a7" = 10)', filter.filterTerm)
			}
			else {
				// no filter should be present
				assertNull(filter)
			}
		}

		JoinParameter param = CellUtil.getFirstParameter(migrated[0], JoinFunction.PARAMETER_JOIN).as(JoinParameter)
		assertJoinOrder(param, ['A7', 'A8'])

		// there should be a condition on the join focus, also in the order
		assertNotNull(param.types[0].filter)

		// there should also be a filter in the condition
		def base = param.conditions.collect { it.baseProperty }.findAll { it.type.displayName == 'A7' }.toList()
		assertEquals(1, base.size())
		assertNotNull(base[0].filter)

		// there should be a message about the condition having been translated automatically
		def messages = getMigrationMessages(migrated[0])
		assertTrue(messages.any { msg ->
			msg.text.toLowerCase().contains('condition')
		})
	}

	// helpers

	void assertJoinOrder(JoinParameter param, List<String> expected) {
		def names = []
		param.types?.each { type ->
			names << type.type.name.localPart
		}

		assertEquals(expected, names)
	}

}
