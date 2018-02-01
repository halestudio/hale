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
import eu.esdihumboldt.hale.common.align.merge.impl.DefaultMergeCellMigrator
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.MutableCell
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction

/**
 * Merge tests related to the FormattedString function.
 * 
 * @author Simon Templer
 */
class FormattedStringMergeTest extends AbstractMergeCellMigratorTest {

	@Test
	void migrateRenameFormattedString() {
		def toMigrate = this.class.getResource('/testcases/formattedstring-abstract1/B-to-C.halex')
		def cellId = 'FormattedString'

		def matching = this.class.getResource('/testcases/formattedstring-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// checks
		checkAbstract1(migrated)
	}

	@Test
	void mergeRenameFormattedString() {
		def toMigrate = this.class.getResource('/testcases/formattedstring-abstract1/B-to-C.halex')
		def cellId = 'FormattedString'

		def matching = this.class.getResource('/testcases/formattedstring-abstract1/A-to-B.halex')

		def migrated = mergeWithMigrator(new DefaultMergeCellMigrator(), cellId, toMigrate, matching)

		// checks
		checkAbstract1(migrated)
	}

	private void checkAbstract1(List<MutableCell> results) {
		assertEquals(1, results.size())
		def migrated = results[0]
		JaxbAlignmentIO.printCell(migrated, System.out)

		// target
		assertCellTargetEquals(migrated, ['C1', 'xyz'])

		// sources
		assertCellSourcesEqual(migrated, ['A1', 'a'], ['A1', 'b'], ['A1', 'c'])

		// parameter
		def pattern = CellUtil.getFirstParameter(migrated, FormattedStringFunction.PARAMETER_PATTERN).as(String.class)
		assertNotNull('Format pattern', pattern)
		assertEquals('Format pattern', '{a}-{b}-{c}', pattern)
	}
}
