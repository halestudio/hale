/*
 * Copyright (c) 2024 wetransform GmbH
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

import org.junit.Test

import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter

/**
 * Test for retaining conditions during Merge.
 * 
 * @author Simon Templer
 */
class JoinRetainConditionsTest extends AbstractMergeCellMigratorTest {

	@Test
	void testRetypeCondition() {
		def toMigrate = this.class.getResource('/testcases/retain-join-conditions-retype/S-to-T.halex')
		def cellId = 'SabcT'

		def matching = this.class.getResource('/testcases/retain-join-conditions-retype/ABC-to-S.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks

		// filter
		assertEquals(1, migrated.size())
		JaxbAlignmentIO.printCell(migrated[0], System.out)

		def expectedFilterA = 'NOT (a = \'100\')'
		def expectedFilterB = '(b IN (\'1000\',\'1001\'))'
		def expectedFilterC = 'NOT (c IS NULL)'

		assertNotNull(migrated[0].source)
		assertEquals(3, migrated[0].source.size())
		Collection<? extends Entity> source = migrated[0].source.values()
		((Collection<Entity>) source).each { e ->
			def filter = e.definition.filter
			if (e.definition.definition.displayName == 'A') {
				// expect filter part to have been propagated to A
				assertNotNull(filter)
				assertEquals(expectedFilterA, filter.filterTerm)
			}
			else if (e.definition.definition.displayName == 'B') {
				// expect filter part to have been propagated to A
				assertNotNull(filter)
				assertEquals(expectedFilterB, filter.filterTerm)
			}
			else if (e.definition.definition.displayName == 'C') {
				// expect filter part to have been propagated to A
				assertNotNull(filter)
				assertEquals(expectedFilterC, filter.filterTerm)
			}
			else {
				fail('Unexpected entity')
			}
		}
		JoinParameter param = CellUtil.getFirstParameter(migrated[0], JoinFunction.PARAMETER_JOIN).as(JoinParameter)
		assertJoinOrder(param, ['A', 'B', 'C'])

		// there should be a condition on all join types, also in the order
		def filter = param.types[0].filter
		assertNotNull(filter)
		assertEquals(expectedFilterA, filter.filterTerm)

		filter = param.types[1].filter
		assertNotNull(filter)
		assertEquals(expectedFilterB, filter.filterTerm)

		filter = param.types[2].filter
		assertNotNull(filter)
		assertEquals(expectedFilterC, filter.filterTerm)

		// there should also be a filter in the condition for each type

		// base properties
		def base = param.conditions.collect { it.baseProperty }.findAll { it.type.displayName == 'A' }.toList()
		assertEquals(1, base.size())
		assertNotNull(base[0].filter)
		assertEquals(expectedFilterA, base[0].filter.filterTerm)

		base = param.conditions.collect { it.baseProperty }.findAll { it.type.displayName == 'B' }.toList()
		assertEquals(1, base.size())
		assertNotNull(base[0].filter)
		assertEquals(expectedFilterB, base[0].filter.filterTerm)

		// join properties
		def join = param.conditions.collect { it.joinProperty }.findAll { it.type.displayName == 'B' }.toList()
		assertEquals(1, join.size())
		assertNotNull(join[0].filter)
		assertEquals(expectedFilterB, join[0].filter.filterTerm)

		join = param.conditions.collect { it.joinProperty }.findAll { it.type.displayName == 'C' }.toList()
		assertEquals(1, join.size())
		assertNotNull(join[0].filter)
		assertEquals(expectedFilterC, join[0].filter.filterTerm)

		// there should be a message about the condition having been translated automatically
		def messages = getMigrationMessages(migrated[0])
		assertTrue(messages.any { msg ->
			msg.text.toLowerCase().contains('condition')
		})
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
				assertEquals('NOT (a1 = \'NIL\')', filter.filterTerm)
			}
			else {
				assertEquals('A2', e.definition.definition.displayName)

				// there should be no filter
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
		// there should be a message about the filter being removed from A2
		assertTrue(messages.any { msg ->
			msg.text.startsWith('A2') && msg.text.toLowerCase().contains('was removed')
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
