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

import org.junit.Test

import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO
import eu.esdihumboldt.hale.common.align.merge.functions.JoinMergeMigrator
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.MutableCell
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter

/**
 * Tests for merging Joins with a Join.
 * 
 * @author Simon Templer
 */
class JoinMergeTest extends AbstractMergeCellMigratorTest {

	@Test
	void mergeJoinsWithJoin_sample_hydro() {
		def toMigrate = this.class.getResource('/testcases/sample-hydro/B-to-C.halex')
		def cellId = 'LakeAndLakeFlowToStandingWater'

		def matching = this.class.getResource('/testcases/sample-hydro/A-to-B.halex')
		def match1Id = 'LakeJoin'
		def match2Id = 'LakeFlowJoin'

		def migrated = mergeWithMigrator(new JoinMergeMigrator(), cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)
		def match1 = getProject(matching).alignment.getCell(match1Id)
		def match2 = getProject(matching).alignment.getCell(match2Id)

		// general checks
		verifyJoinsJoin(migrated, original, [match1, match2])

		// specific checks
		assertEquals(1, migrated.size())
		migrated = migrated[0]
		JaxbAlignmentIO.printCell(migrated, System.out)

		// target
		assertCellTargetEquals(migrated, ['StandingWater'])

		// sources
		assertCellSourcesEqual(migrated, ['Lake'], ['LakeProperties'], ['Connection'])

		// parameters
		JoinParameter param = CellUtil.getFirstParameter(migrated, JoinFunction.PARAMETER_JOIN).as(JoinParameter)

		// order
		assertJoinOrder(param, [
			'Lake',
			'LakeProperties',
			'Connection'
		])

		// conditions
		assertJoinConditions(param, [
			[
				['Lake', 'id'],
				[
					'LakeProperties',
					'lakeId']
			],
			[
				['Lake', 'id'],
				[
					'Connection',
					'standingId']
			]
		])
	}

	@Test
	void mergeJoinsWithJoin_abstract1() {
		def toMigrate = this.class.getResource('/testcases/abstract1/B-to-C.halex')
		def cellId = 'B1B2toC1'

		def matching = this.class.getResource('/testcases/abstract1/A-to-B.halex')
		def match1Id = 'A1A2toB1'
		def match2Id = 'A3A4toB2'

		def migrated = mergeWithMigrator(new JoinMergeMigrator(), cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)
		def match1 = getProject(matching).alignment.getCell(match1Id)
		def match2 = getProject(matching).alignment.getCell(match2Id)

		// general checks
		verifyJoinsJoin(migrated, original, [match1, match2])

		// specific checks
		assertEquals(1, migrated.size())
		migrated = migrated[0]
		JaxbAlignmentIO.printCell(migrated, System.out)

		// target
		assertCellTargetEquals(migrated, ['C1'])

		// sources
		assertCellSourcesEqual(migrated, ['A1'], ['A2'], ['A3'], ['A4'])

		// parameters
		JoinParameter param = CellUtil.getFirstParameter(migrated, JoinFunction.PARAMETER_JOIN).as(JoinParameter)

		// order
		assertJoinOrder(param, [
			'A1',
			'A2',
			'A3',
			'A4'
		])

		// conditions
		assertJoinConditions(param, [
			[
				['A1', 'a1'],
				['A2', 'a1']
			],
			[
				['A2', 'a1'],
				['A3', 'a1']
			],
			[
				['A3', 'a3'],
				['A4', 'a3'] //
			]
		])
	}

	// common tests

	private void verifyJoinsJoin(List<MutableCell> migrated, Cell original, List<Cell> matches) {
		// number of cells
		assertEquals('Joins and Join should be combined to one cell', 1, migrated.size())
		def cell = migrated[0]
		assertNotNull(cell)

		// transformation function
		assertEquals('Join and Join should be combined to a Join', JoinFunction.ID, cell.transformationIdentifier)
		//XXX Groovy Join possible as well?

		// target

		// target must be the same as original
		assertEquals(original.target, cell.target)

		// sources

		// sources must be from set of match sources
		Set<EntityDefinition> matchSources = new HashSet<>()
		matches.each { match ->
			match.source?.values().each { entity ->
				matchSources << entity.definition
			}
		}
		def remaining = new ArrayList<>(matchSources)
		cell.source.values().each { entity ->
			assertTrue("Source ${entity.definition} not expected", matchSources.contains(entity.definition))
			remaining.remove(entity.definition)
		}
		assertTrue("Sources expected but not present: ${remaining}", remaining.empty)

		// parameters?
		// -> better to test individually
	}

	// helpers

	void assertJoinOrder(JoinParameter param, List<String> expected) {
		def names = []
		param.types?.each { type ->
			names << type.type.name.localPart
		}

		assertEquals(expected, names)
	}

	void assertJoinConditions(JoinParameter param, List<List<List<String>>> expected) {
		// express conditions as lists of string lists as well
		def conditions = []
		param.conditions.each { condition ->
			conditions << [
				toSimpleDef(condition.baseProperty),
				toSimpleDef(condition.joinProperty)
			]
		}

		def remaining = new ArrayList<>(expected)

		conditions.each { cond ->
			assertTrue("Condition $cond not expected", remaining.contains(cond))
			remaining.remove(cond);
		}

		assertTrue("Expected conditions not found ${remaining}", remaining.empty)
	}

}