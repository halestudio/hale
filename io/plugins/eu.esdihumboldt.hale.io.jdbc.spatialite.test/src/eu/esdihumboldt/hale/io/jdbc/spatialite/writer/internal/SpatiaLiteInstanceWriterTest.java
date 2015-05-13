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

import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.SOURCE_INSTANCES_COUNT;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.SOUURCE_TYPE_LOCAL_NAME;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.checkInstances;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.checkType;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.getSourceTempFilePath;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.getTargetTempFilePath;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.readInstances;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.readSchema;
import static eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil.writeInstances;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil;

/**
 * Test class for {@link SpatiaLiteInstanceWriter}. The tests are automatically
 * skipped if the SpatiaLite extension can't be found on the system.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteInstanceWriterTest {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteInstanceWriterTest.class);

	/**
	 * Wait for needed services to be running and create source / target
	 * database files in a system dependent temporary folder.
	 * 
	 * @throws IOException if temp files can't be created
	 */
	@BeforeClass
	public static void waitForServices() throws IOException {
		TestUtil.startConversionService();

		SpatiaLiteTestUtil.createSourceTempFile();
		SpatiaLiteTestUtil.createTargetTempFile();
	}

	/**
	 * Delete any temporary file created for the test.
	 */
	@AfterClass
	public static void cleanUp() {
		SpatiaLiteTestUtil.deleteSourceTempFile();
		SpatiaLiteTestUtil.deleteTargetTempFile();
	}

	/**
	 * Test - reads data from a source SpatiaLite database, writes them to a
	 * target SpatiaLite database and checks the results.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testWrite() throws Exception {
		if (!SpatiaLiteTestUtil.isSpatiaLiteExtensionAvailable()) {
			log.info("Skipping test because SpatiaLite extension is not available");
			return;
		}

		Map<String, Object> propertyMap = new HashMap<String, Object>();
		for (int i = 0; i < SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_NAMES.length; i++) {
			String key = SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_NAMES[i];
			Object value = SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_VALUES[i];
			propertyMap.put(key, value);
		}

		// ****** read Schema ******//

		Schema schema = readSchema(getSourceTempFilePath());
		assertNotNull(schema);
		assertEquals(1, schema.getMappingRelevantTypes().size());

		// Test properties
		TypeDefinition schemaType = schema.getMappingRelevantTypes().iterator().next();
		// Check every property for their existence
		checkType(schemaType, SOUURCE_TYPE_LOCAL_NAME, propertyMap.keySet());

		// ****** read Instances ******//

		InstanceCollection instances = readInstances(schema, getSourceTempFilePath());
		assertTrue(instances.hasSize());
		assertEquals(SOURCE_INSTANCES_COUNT, instances.size());

		checkInstances(instances, propertyMap);

		// ****** write Instances ******//

		// check target DB is empty
		InstanceCollection targetInstances = readInstances(schema, getTargetTempFilePath());
		assertTrue(targetInstances.hasSize());
		assertEquals(0, targetInstances.size());

		writeInstances(getTargetTempFilePath(), instances);

		// re-read instances to check they were written correctly
		targetInstances = readInstances(schema, getTargetTempFilePath());
		assertTrue(targetInstances.hasSize());
		assertEquals(SOURCE_INSTANCES_COUNT, targetInstances.size());

		checkInstances(targetInstances, propertyMap);

	}

}
