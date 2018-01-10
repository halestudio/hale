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

import eu.esdihumboldt.hale.common.align.merge.impl.DefaultMergeCellMigrator
import eu.esdihumboldt.hale.common.align.merge.test.AbstractMergeCellMigratorTest

/**
 * Tests for default merge cell migrator.
 * 
 * @author Simon Templer
 */
class DefaultMergeCellMigratorTest extends AbstractMergeCellMigratorTest {

	@Test
	void mergeJoinRetype() {
		def migrator = new DefaultMergeCellMigrator()
		def migrated = runMergeMigrator(migrator, 'RiverToWatercourse',
				this.class.getResource('/testcases/sample-hydro/B-to-C.halex'),
				this.class.getResource('/testcases/sample-hydro/A-to-B.halex'))

		assertEquals(1, migrated.size())
		//TODO more checks
	}

	//TODO more tests
}
