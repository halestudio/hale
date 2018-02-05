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

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.cst.functions.groovy.GroovyRetype
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO
import eu.esdihumboldt.hale.common.align.merge.impl.DefaultMergeCellMigrator
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.MutableCell
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction

/**
 * Tests for default merge cell migrator.
 * 
 * @author Simon Templer
 */
class DefaultMergeCellMigratorTest extends AbstractMergeCellMigratorTest {

	@Test
	void mergeJoinWithRetypeDef() {
		// explicitly use default migrator
		def migrator = new DefaultMergeCellMigrator()

		def toMigrate = this.class.getResource('/testcases/sample-hydro/B-to-C.halex')
		def cellId = 'RiverToWatercourse'

		def matching = this.class.getResource('/testcases/sample-hydro/A-to-B.halex')
		def matchId = 'RiverJoin'

		def migrated = mergeWithMigrator(migrator, cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)
		def match = getProject(matching).alignment.getCell(matchId)

		verifyJoinRetype(migrated, original, match)
	}

	@Test
	void mergeJoinWithRetype() {
		def toMigrate = this.class.getResource('/testcases/sample-hydro/B-to-C.halex')
		def cellId = 'RiverToWatercourse'

		def matching = this.class.getResource('/testcases/sample-hydro/A-to-B.halex')
		def matchId = 'RiverJoin'

		def migrated = merge(cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)
		def match = getProject(matching).alignment.getCell(matchId)

		verifyJoinRetype(migrated, original, match)
	}

	private void verifyJoinRetype(List<MutableCell> migrated, Cell original, Cell match) {
		// number of cells
		assertEquals('Join and Retype should be combined to one cell', 1, migrated.size())
		def cell = migrated[0]
		assertNotNull(cell)

		// transformation function
		assertEquals('Join and Retype should be combined to a Join', JoinFunction.ID, cell.transformationIdentifier)

		// target (should be same as of Retype)
		ListMultimap<String, Object> targets = cell.target
		assertNotNull(targets)
		assertEquals(1, targets.size())
		def target = CellUtil.getFirstEntity(targets)
		assertNotNull(target)
		def orgTarget = CellUtil.getFirstEntity(original.target)
		assertNotNull(orgTarget)
		assertEquals('Target of new Join must be the same as the target of the Retype', orgTarget, target)

		// sources (should be same as of Join)
		def sources = cell.source
		def matchSources = match.source
		assertEquals('Sources must be the same as for the matching Join', matchSources, sources)

		// parameters

		// Join type order (should be the same as for the matching cell)
		def typeOrder = CellUtil.getFirstParameter(cell, JoinFunction.JOIN_TYPES)
		assertNotNull(typeOrder)
		def matchTypeOrder = CellUtil.getFirstParameter(match, JoinFunction.JOIN_TYPES)
		assertEquals('Join order must be the same as for the matching Join', matchTypeOrder, typeOrder)

		// Join conditions (should be the same as for the matching cell)
		def conditions = CellUtil.getFirstParameter(cell, JoinFunction.PARAMETER_JOIN)
		assertNotNull(conditions)
		def matchConditions = CellUtil.getFirstParameter(match, JoinFunction.PARAMETER_JOIN)
		assertEquals('Join conditions must be the same as for the matching Join', matchConditions, conditions)
	}

	@Test
	void testNotes1() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B1ba-C1ca'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		assertEquals(1, migrated.size())
		migrated = migrated[0]

		JaxbAlignmentIO.printCell(migrated, System.out)

		// simple rename combination
		assertEquals(RenameFunction.ID, migrated.transformationIdentifier)

		// target
		assertCellTargetEquals(migrated, ['C1', 'ca'])

		// sources
		assertCellSourcesEqual(migrated, ['A1', 'aa'])

		// notes should be taken from cell to migrate (XXX in future change to include source notes as well?)
		def notes = CellUtil.getNotes(migrated)
		assertNotNull(notes)
		assertEquals('ba to ca', notes)
	}

	@Test
	void testNotes2() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B1-C1'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		assertEquals(1, migrated.size())
		migrated = migrated[0]

		JaxbAlignmentIO.printCell(migrated, System.out)

		// Groovy Retype
		assertEquals(GroovyRetype.ID, migrated.transformationIdentifier)

		// target
		assertCellTargetEquals(migrated, ['C1'])

		// sources
		assertCellSourcesEqual(migrated, ['A1'])

		// notes should be taken from cell to migrate (XXX in future change to include source notes as well?)
		def notes = CellUtil.getNotes(migrated)
		assertNotNull(notes)
		assertEquals('B1 to C1', notes)
	}

	@Test
	void testCondition1() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B1bc-C1cc'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		assertEquals(1, migrated.size())
		migrated = migrated[0]

		JaxbAlignmentIO.printCell(migrated, System.out)

		// simple rename combination
		assertEquals(RenameFunction.ID, migrated.transformationIdentifier)

		// target
		assertCellTargetEquals(migrated, ['C1', 'cc'])

		// sources
		assertCellSourcesEqual(migrated, ['A1', 'ac'])

		// there should be a message informing to check the condition
		def messages = getMigrationMessages(migrated)
		assertTrue(messages.size() > 0)
		assertTrue(messages.any { msg ->
			msg.text.toLowerCase().contains('condition')
		})
	}

	@Test
	void testMatchCondition1() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B1bd-C1cd'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		assertEquals(1, migrated.size())
		migrated = migrated[0]

		JaxbAlignmentIO.printCell(migrated, System.out)

		// simple rename combination
		assertEquals(RenameFunction.ID, migrated.transformationIdentifier)

		// target
		assertCellTargetEquals(migrated, ['C1', 'cd'])

		// sources
		assertCellSourcesEqual(migrated, ['A1', 'ad'])

		// the condition should be present on the source
		def filter = CellUtil.getFirstEntity(migrated.source).definition.propertyPath[0].condition.filter
		assertNotNull(filter)
		assertEquals('value = \'green lantern\'', filter.filterTerm)

		// there should be no message about the condition
		def messages = getMigrationMessages(migrated)
		assertFalse(messages.any { msg ->
			msg.text.toLowerCase().contains('condition')
		})
	}

	@Test
	void testGroovyWarningScript() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B2ba-C2ca'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		scriptCheck(migrated)
	}

	private void scriptCheck(def migrated) {
		assertEquals(1, migrated.size())
		migrated = migrated[0]

		JaxbAlignmentIO.printCell(migrated, System.out)

		// there should be a message informing to check the script
		def messages = getMigrationMessages(migrated)
		assertTrue(messages.size() > 0)
		assertTrue(messages.any { msg ->
			msg.text.toLowerCase().contains('script')
		})
	}

	@Test
	void testGroovyWarningRetype() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B5-C5'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		scriptCheck(migrated)
	}

	@Test
	void testGroovyWarningMerge() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B6-C6'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		scriptCheck(migrated)
	}

	@Test
	void testGroovyWarningRetypeTarget() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B2-C2'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		scriptCheck(migrated)
	}

	@Test
	void testGroovyWarningMergeTarget() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B3-C3'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		scriptCheck(migrated)
	}

	@Test
	void testGroovyWarningCreateTarget() {
		def toMigrate = this.class.getResource('/testcases/properties-abstract1/B-to-C.halex')
		def cellId = 'B4-C4'

		def matching = this.class.getResource('/testcases/properties-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		scriptCheck(migrated)
	}

	@Test
	void testFilterSame1() {
		def toMigrate = this.class.getResource('/testcases/filter-same/B-to-C.halex')
		def cellId = 'C1'

		def matching = this.class.getResource('/testcases/filter-same/B-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		filterCheckSame(migrated, "x <> 'Y'")
	}

	@Test
	void testFilterSame2() {
		def toMigrate = this.class.getResource('/testcases/filter-same/B-to-C.halex')
		def cellId = 'c1'

		def matching = this.class.getResource('/testcases/filter-same/B-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		assertEquals(1, migrated.size())
		migrated = migrated[0]

		//XXX found as match even though source type has a filter

		// simple rename combination
		assertEquals(RenameFunction.ID, migrated.transformationIdentifier)
	}

	@Test
	void testFilterSame3() {
		def toMigrate = this.class.getResource('/testcases/filter-same/B-to-C.halex')
		def cellId = 'x'

		def matching = this.class.getResource('/testcases/filter-same/B-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		filterCheckSame(migrated, "value = 'X'")
	}

	@Test
	void testFilterSame4() {
		def toMigrate = this.class.getResource('/testcases/filter-same/B-to-C.halex')
		def cellId = 'y'

		def matching = this.class.getResource('/testcases/filter-same/B-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		filterCheckSame(migrated, "value = 'Y'")
	}

	@Test
	void testFilterSame5() {
		def toMigrate = this.class.getResource('/testcases/filter-same/B-to-C.halex')
		def cellId = 'z'

		def matching = this.class.getResource('/testcases/filter-same/B-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		filterCheckSame(migrated, "value = 'Z'")
	}

	@Test
	void testFilterSame6() {
		def toMigrate = this.class.getResource('/testcases/filter-same/B-to-C.halex')
		def cellId = 'x2'

		def matching = this.class.getResource('/testcases/filter-same/B-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// do checks
		filterCheckSame(migrated, "value = 'X'")
	}

	private void filterCheckSame(List<Cell> cells, String expectedFilter) {
		assertEquals(1, cells.size())
		def migrated = cells[0]

		JaxbAlignmentIO.printCell(migrated, System.out)

		// the condition should be present on the source
		def source = CellUtil.getFirstEntity(migrated.source).definition
		def filter = source.propertyPath.empty ? source.filter : source.propertyPath[0].condition?.filter
		assertNotNull(filter)
		assertEquals(expectedFilter, filter.filterTerm)

		// there should be no message about the condition because the new source is the same!
		def messages = getMigrationMessages(migrated)
		assertFalse(messages.any { msg ->
			msg.text.toLowerCase().contains('condition')
		})
	}
}
