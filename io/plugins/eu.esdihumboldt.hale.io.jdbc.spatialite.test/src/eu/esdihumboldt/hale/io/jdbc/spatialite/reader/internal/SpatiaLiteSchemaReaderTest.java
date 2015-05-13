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

import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.SOUURCE_TYPE_LOCAL_NAME;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.checkType;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.getSourceTempFilePath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil;

/**
 * Test class for {@link SpatiaLiteSchemaReader}. The tests are automatically
 * skipped if the SpatiaLite extension can't be found on the system.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteSchemaReaderTest {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteSchemaReaderTest.class);

	/**
	 * Wait for needed services to be running and create source database file in
	 * a system dependent temporary folder.
	 * 
	 * @throws IOException if temp file can't be created
	 */
	@BeforeClass
	public static void waitForServices() throws IOException {
		TestUtil.startConversionService();

		SpatiaLiteTestUtil.createSourceTempFile();
	}

	/**
	 * Delete any temporary file created for the test.
	 */
	@AfterClass
	public static void cleanUp() {
		SpatiaLiteTestUtil.deleteSourceTempFile();
	}

	/**
	 * Test - reads a sample SpatiaLite schema
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testRead() throws Exception {
		if (!SpatiaLiteTestUtil.isSpatiaLiteExtensionAvailable()) {
			log.info("Skipping test because SpatiaLite extension is not available");
			return;
		}

		Set<String> propertyNames = new HashSet<String>(
				Arrays.asList(SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_NAMES));

		SpatiaLiteSchemaReader schemaReader = new SpatiaLiteSchemaReader();
		schemaReader.setSource(new FileIOSupplier(new File(getSourceTempFilePath())));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		assertEquals(1, schema.getMappingRelevantTypes().size());
		TypeDefinition type = schema.getMappingRelevantTypes().iterator().next();

		checkType(type, SOUURCE_TYPE_LOCAL_NAME, propertyNames);
	}

}
