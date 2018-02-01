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
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest

/**
 * Merge tests related to the FormattedString function.
 * 
 * @author Simon Templer
 */
class FormattedStringMergeTest extends AbstractMergeCellMigratorTest {

	@Test
	void mergeRenameFormattedString() {
		def toMigrate = this.class.getResource('/testcases/formattedstring-abstract1/B-to-C.halex')
		def cellId = 'FormattedString'

		def matching = this.class.getResource('/testcases/formattedstring-abstract1/A-to-B.halex')
		def match1Id = 'Match1'
		def match2Id = 'Match2'
		def match3Id = 'Match3'

		def migrated = merge(cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)
		def match1 = getProject(matching).alignment.getCell(match1Id)
		def match2 = getProject(matching).alignment.getCell(match2Id)
		def match3 = getProject(matching).alignment.getCell(match3Id)

		// checks

		assertEquals(1, migrated.size())
		migrated = migrated[0]
		JaxbAlignmentIO.printCell(migrated, System.out)

		// target
		assertCellTargetEquals(migrated, ['xyz'])

		// sources
		assertCellSourcesEqual(migrated, ['x'], ['y'], ['z'])

		//TODO
	}
}
