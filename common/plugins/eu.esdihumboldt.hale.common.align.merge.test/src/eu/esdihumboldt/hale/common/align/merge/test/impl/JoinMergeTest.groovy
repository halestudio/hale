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

import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.MutableCell
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction

/**
 * Tests for merging Joins with a Join.
 * 
 * @author Simon Templer
 */
class JoinMergeTest extends AbstractMergeCellMigratorTest {

	@Test
	void mergeJoinsWithJoin() {
		def toMigrate = this.class.getResource('/testcases/sample-hydro/B-to-C.halex')
		def cellId = 'LakeAndLakeFlowToStandingWater'

		def matching = this.class.getResource('/testcases/sample-hydro/A-to-B.halex')
		def match1Id = 'LakeJoin'
		def match2Id = 'LakeFlowJoin'

		def migrated = merge(cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)
		def match1 = getProject(matching).alignment.getCell(match1Id)
		def match2 = getProject(matching).alignment.getCell(match2Id)

		verifyJoinsJoin(migrated, original, [match1, match2])
	}

	private void verifyJoinsJoin(List<MutableCell> migrated, Cell original, List<Cell> matches) {
		// number of cells
		assertEquals('Joins and Join should be combined to one cell', 1, migrated.size())
		def cell = migrated[0]
		assertNotNull(cell)

		// transformation function
		assertEquals('Join and Join should be combined to a Join', JoinFunction.ID, cell.transformationIdentifier)
		//XXX Groovy Join possible as well?

		//TODO target

		//TODO sources

		//TODO parameters
	}
}