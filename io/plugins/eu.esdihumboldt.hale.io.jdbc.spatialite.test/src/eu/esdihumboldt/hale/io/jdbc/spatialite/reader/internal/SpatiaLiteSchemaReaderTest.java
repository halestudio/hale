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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.jdbc.spatialite.test.SpatiaLiteTestUtil;

/**
 * TODO Type description
 * 
 * @author stefano
 */
public class SpatiaLiteSchemaReaderTest {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteSchemaReaderTest.class);

	/**
	 * Wait for needed services to be running
	 * 
	 * @throws IOException
	 */
	@BeforeClass
	public static void waitForServices() throws IOException {
		TestUtil.startConversionService();

		SpatiaLiteTestUtil.createSourceTempFile();
	}

	@AfterClass
	public static void cleanUp() {
		SpatiaLiteTestUtil.deleteSourceTempFile();
	}

	/**
	 * Test for given property names and property types
	 * 
	 * @throws Exception the Exception thrown if the test fails
	 */
	@Test
	public void testRead() throws Exception {
		if (!SpatiaLiteTestUtil.isSpatiaLiteExtensionAvailable()) {
			log.info("Skipping test because SpatiaLite extension is not available");
			return;
		}

		Set<String> properties = new HashSet<String>(
				Arrays.asList(SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_NAMES));

		SpatiaLiteSchemaReader schemaReader = new SpatiaLiteSchemaReader();
		schemaReader.setSource(new FileIOSupplier(new File(SpatiaLiteTestUtil
				.getSourceTempFilePath())));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		assertEquals(1, schema.getMappingRelevantTypes().size());
		TypeDefinition type = schema.getMappingRelevantTypes().iterator().next();
		assertTrue(type.getName().getLocalPart().equals(SpatiaLiteTestUtil.SOUURCE_TYPE_LOCAL_NAME));

		Iterator<? extends ChildDefinition<?>> it = type.getChildren().iterator();
		while (it.hasNext()) {
			assertTrue(properties.contains(it.next().getName().getLocalPart()));
		}
	}
}
