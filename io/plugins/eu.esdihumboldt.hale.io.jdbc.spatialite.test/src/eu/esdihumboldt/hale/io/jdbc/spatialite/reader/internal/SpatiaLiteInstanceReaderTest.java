/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc.spatialite.reader.internal;

import org.junit.Test;

import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestSuite;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestSuiteVersion3;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestSuiteVersion4;

/**
 * Test class for {@link SpatiaLiteInstanceReader}. The tests are automatically
 * skipped if the SpatiaLite extension can't be found on the system.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteInstanceReaderTest {

	/**
	 * Wait for needed services to be running.
	 */
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	/**
	 * Invoke {@link SpatiaLiteTestSuite#instanceReaderTest()} on the test suite
	 * class for SpatiaLite version 3 or less.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadVersion3() throws Exception {
		SpatiaLiteTestSuite testSuite = new SpatiaLiteTestSuiteVersion3();

		testRead(testSuite);
	}

	/**
	 * Invoke {@link SpatiaLiteTestSuite#instanceReaderTest()} on the test suite
	 * class for SpatiaLite version 4 or more.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testReadVersion4() throws Exception {
		SpatiaLiteTestSuite testSuite = new SpatiaLiteTestSuiteVersion4();

		testRead(testSuite);
	}

	private void testRead(SpatiaLiteTestSuite testSuite) throws Exception {
		try {
			testSuite.createSourceTempFile();
			testSuite.instanceReaderTest();
		} finally {
			testSuite.deleteSourceTempFile();
		}

	}
}
