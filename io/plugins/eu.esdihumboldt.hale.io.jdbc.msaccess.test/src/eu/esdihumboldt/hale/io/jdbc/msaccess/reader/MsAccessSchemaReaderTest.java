/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.io.jdbc.msaccess.reader;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.hale.io.jdbc.msaccess.test.MsAccessDataReader;
import eu.esdihumboldt.hale.io.jdbc.msaccess.test.MsAccessDataReaderAccdb;
import eu.esdihumboldt.hale.io.jdbc.msaccess.test.MsAccessDataReaderTestSuit;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Contains tests for MsAccess Schema Reader
 * 
 * @author Arun
 */
@Features("Databases")
@Stories("MS Access")
public class MsAccessSchemaReaderTest {

	/**
	 * Test for Schema Reader from MS Access database of mdb extension
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMsAccessSchemaReaderForMdbTest() throws Exception {
		MsAccessDataReaderTestSuit testSuite = new MsAccessDataReader();
		try {
			testSuite.createSourceTempFile();
			testSuite.schemaReaderTest();
		} finally {
			testSuite.deleteSourceTempFile();
		}
	}

	/**
	 * Test for Schema Reader from MS Access database of accdb extension.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Ignore // Will test afterwards once finished everything with MDB extension
			// first.
	@Test
	public void testMsAccessSchemaReaderForAccdbTest() throws Exception {
		MsAccessDataReaderTestSuit testSuite = new MsAccessDataReaderAccdb();
		try {
			testSuite.createSourceTempFile();
			testSuite.schemaReaderTest();
		} finally {
			testSuite.deleteSourceTempFile();
		}
	}

}
