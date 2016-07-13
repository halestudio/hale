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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.esdihumboldt.hale.io.jdbc.msaccess.test.MsAccessDataReader;
import eu.esdihumboldt.hale.io.jdbc.msaccess.test.MsAccessDataReaderTestSuit;

/**
 * TODO Type description
 * 
 * @author Arun
 */
public class MsAccessSchemaReaderTest {

	/**
	 * Test for Schema Reader from MS Access database
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMsAccessDataReaderTest() throws Exception {
		MsAccessDataReaderTestSuit testSuite = new MsAccessDataReader();
		try {
			testSuite.createSourceTempFile();
			testSuite.schemaReaderTest();
		} finally {
			testSuite.deleteSourceTempFile();
		}
	}

	/**
	 * Testing connection and data reading from access database
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testMsAccessGetData() throws Exception {
		MsAccessDataReaderTestSuit obj = new MsAccessDataReader();
		try {
			obj.createSourceTempFile();
			String rs = obj.getFirstData();
			assertTrue(rs != null);
			assertTrue(rs.length() > 0);
		} finally {
			obj.deleteSourceTempFile();
		}
	}

}
