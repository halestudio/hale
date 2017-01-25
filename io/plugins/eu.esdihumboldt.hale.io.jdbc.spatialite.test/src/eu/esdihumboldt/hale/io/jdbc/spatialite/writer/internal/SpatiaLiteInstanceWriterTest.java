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

package eu.esdihumboldt.hale.io.jdbc.spatialite.writer.internal;

import org.junit.Test;

import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestSuite;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestSuiteVersion3;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestSuiteVersion4;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test class for {@link SpatiaLiteInstanceWriter}. The tests are automatically
 * skipped if the SpatiaLite extension can't be found on the system.
 * 
 * @author Stefano Costa, GeoSolutions
 */
@Features("Databases")
@Stories("SpatiaLite")
public class SpatiaLiteInstanceWriterTest {

	/**
	 * Wait for needed services to be running.
	 */
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	/**
	 * Invoke {@link SpatiaLiteTestSuite#instanceWriterTest()} on the test suite
	 * class for SpatiaLite version 3 or less.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testWriteVersion3() throws Exception {
		SpatiaLiteTestSuite testSuite = new SpatiaLiteTestSuiteVersion3();

		testWrite(testSuite);
	}

	/**
	 * Invoke {@link SpatiaLiteTestSuite#instanceWriterTest()} on the test suite
	 * class for SpatiaLite version 4 or higher.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testWriteVersion4() throws Exception {
		SpatiaLiteTestSuite testSuite = new SpatiaLiteTestSuiteVersion4();

		testWrite(testSuite);
	}

	private void testWrite(SpatiaLiteTestSuite testSuite) throws Exception {
		try {
			testSuite.createSourceTempFile();
			testSuite.createTargetTempFile();

			testSuite.instanceWriterTest();
		} finally {
			testSuite.deleteSourceTempFile();
			testSuite.deleteTargetTempFile();
		}
	}

}
