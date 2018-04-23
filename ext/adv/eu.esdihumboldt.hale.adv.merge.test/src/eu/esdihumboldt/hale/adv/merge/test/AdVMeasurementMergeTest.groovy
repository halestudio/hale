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

package eu.esdihumboldt.hale.adv.merge.test

import static org.junit.Assert.*

import org.junit.Test

import eu.esdihumboldt.hale.adv.merge.AdVMeasurementMigrator;
import eu.esdihumboldt.hale.common.align.io.impl.JaxbAlignmentIO
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.MutableCell
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction

/**
 * Test the AdVMeasurementMigrator.
 * 
 * @author Simon Templer
 */
class AdVMeasurementMergeTest extends AbstractMergeCellMigratorTest {

	@Test
	void migrateRenameFormattedString() {
		def toMigrate = this.class.getResource('/testcases/measurement1/B-to-C.halex')
		def cellId = 'ConvertUnit'

		def matching = this.class.getResource('/testcases/measurement1/A-to-B.halex')

		def migrated = merge(cellId, toMigrate, matching)

		// checks
		check1(migrated)
	}

	@Test
	void mergeRenameFormattedString() {
		def toMigrate = this.class.getResource('/testcases/measurement1/B-to-C.halex')
		def cellId = 'ConvertUnit'

		def matching = this.class.getResource('/testcases/measurement1/A-to-B.halex')

		def migrated = mergeWithMigrator(new AdVMeasurementMigrator(), cellId, toMigrate, matching)

		// checks
		check1(migrated)
	}

	private void check1(List<MutableCell> results) {
		assertEquals(1, results.size())
		def migrated = results[0]
		JaxbAlignmentIO.printCell(migrated, System.out)

		// function
		assertEquals('Function', AssignFunction.ID_BOUND, migrated.transformationIdentifier)

		// target
		assertCellTargetEquals(migrated, ['C1', 'length', 'uom'])

		// sources
		assertCellSourcesEqual(migrated, ['A1', 'length'])

		// parameter
		def value = CellUtil.getFirstParameter(migrated, AssignFunction.PARAMETER_VALUE).as(String.class)
		assertNotNull('Assigned value', value)
		assertEquals('Assigned value', 'm', value)
	}
}
