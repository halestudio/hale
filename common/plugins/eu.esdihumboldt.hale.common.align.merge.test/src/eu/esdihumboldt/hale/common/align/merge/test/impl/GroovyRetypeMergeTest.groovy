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

import eu.esdihumboldt.cst.functions.groovy.GroovyConstants
import eu.esdihumboldt.cst.functions.groovy.GroovyJoin
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO
import eu.esdihumboldt.hale.common.align.merge.functions.GroovyRetypeMergeMigrator
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.MutableCell
import eu.esdihumboldt.hale.common.core.io.Text

/**
 * Merge tests related to the FormattedString function.
 * 
 * @author Simon Templer
 */
class GroovyRetypeMergeTest extends AbstractMergeCellMigratorTest {

	@Test
	void migrateJoinGroovyRetype() {
		def toMigrate = this.class.getResource('/testcases/groovyretype-abstract1/B-to-C.halex')
		def cellId = 'GroovyRetype'

		def matching = this.class.getResource('/testcases/groovyretype-abstract1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)

		// checks
		checkAbstract1(migrated, original)
	}

	@Test
	void mergeJoinGroovyRetype() {
		def toMigrate = this.class.getResource('/testcases/groovyretype-abstract1/B-to-C.halex')
		def cellId = 'GroovyRetype'

		def matching = this.class.getResource('/testcases/groovyretype-abstract1/A-to-B.halex')

		def migrated = mergeWithMigrator(new GroovyRetypeMergeMigrator(), cellId, toMigrate, matching)

		def original = getProject(toMigrate).alignment.getCell(cellId)

		// checks
		checkAbstract1(migrated, original)
	}

	private void checkAbstract1(List<MutableCell> results, Cell original) {
		assertEquals(1, results.size())
		def migrated = results[0]
		JaxbAlignmentIO.printCell(migrated, System.out)

		// target
		assertCellTargetEquals(migrated, ['C1'])

		// sources
		assertCellSourcesEqual(migrated, ['A1'], ['A2'], ['A3'])

		// function
		assertEquals('Function', GroovyJoin.ID, migrated.transformationIdentifier)

		// annotations
		def messages = getMigrationMessages(migrated)
		assertFalse('Message must be present', messages.isEmpty())

		// parameter
		def script = CellUtil.getFirstParameter(migrated, GroovyConstants.PARAMETER_SCRIPT).as(Text.class)
		assertNotNull('Script', script)
		script = script.getText()

		def orgScript = CellUtil.getFirstParameter(original, GroovyConstants.PARAMETER_SCRIPT).as(Text.class).text

		assertTrue(script.contains(orgScript))
	}
}
