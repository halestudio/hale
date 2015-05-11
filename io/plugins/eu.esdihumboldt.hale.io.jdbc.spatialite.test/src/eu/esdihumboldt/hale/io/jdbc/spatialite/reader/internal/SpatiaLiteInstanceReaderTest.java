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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
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
public class SpatiaLiteInstanceReaderTest {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteInstanceReaderTest.class);

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
	 * Test - read a sample SpatiaLite schema and data.
	 * 
	 * @throws Exception , if an error occurs
	 */
	@Test
	public void testReadSimple() throws Exception {
		if (!SpatiaLiteTestUtil.isSpatiaLiteExtensionAvailable()) {
			log.info("Skipping test because SpatiaLite extension is not available");
			return;
		}

		Set<String> properties = new HashSet<String>(
				Arrays.asList(SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_NAMES));
		Map<String, Object> values = new HashMap<String, Object>();
		for (int i = 0; i < SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_NAMES.length; i++) {
			String key = SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_NAMES[i];
			Object value = SpatiaLiteTestUtil.SOUURCE_TYPE_PROPERTY_VALUES[i];
			values.put(key, value);
		}

		// ****** read Schema ******//
		Schema schema = readSchema();
		// Test properties
		TypeDefinition schemaType = schema.getTypes().iterator().next();

		// Check every property for their existence
		Iterator<? extends ChildDefinition<?>> it = schemaType.getChildren().iterator();
		while (it.hasNext()) {
			assertTrue(properties.contains(it.next().getName().getLocalPart()));
		}

		// ****** read Instances ******//
		InstanceCollection instances = readInstances(schema);
		assertTrue(instances.hasSize());
		assertEquals(7, instances.size());

		// get Type to check property definition (schema and instance
		// combination)
		TypeDefinition type = instances.iterator().next().getDefinition();
		assertEquals(SpatiaLiteTestUtil.SOUURCE_TYPE_LOCAL_NAME, type.getDisplayName());
		for (ChildDefinition<?> child : type.getChildren()) {
			assertTrue(properties.contains(child.getName().getLocalPart()));
		}

		// Check the values of the first (type) instance
		Instance instance = instances.iterator().next();
		for (String propertyName : properties) {
			Object value = instance.getProperty(QName.valueOf(propertyName))[0];
			if (value instanceof GeometryProperty) {
				assertTrue(((Geometry) values.get(propertyName)).equalsExact(
						((GeometryProperty) value).getGeometry(), 0.000001));
			}
			else {
				assertEquals(values.get(propertyName), value);
			}
		}

	}

	private Schema readSchema() throws Exception {

		SpatiaLiteSchemaReader schemaReader = new SpatiaLiteSchemaReader();
		schemaReader.setSource(new FileIOSupplier(new File(SpatiaLiteTestUtil
				.getSourceTempFilePath())));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		return schemaReader.getSchema();

	}

	private InstanceCollection readInstances(Schema sourceSchema) throws Exception {

		SpatiaLiteInstanceReader instanceReader = new SpatiaLiteInstanceReader();
		instanceReader.setSource(new FileIOSupplier(new File(SpatiaLiteTestUtil
				.getSourceTempFilePath())));
		instanceReader.setSourceSchema(sourceSchema);

		// Test instances
		IOReport report = instanceReader.execute(new LogProgressIndicator());
		assertTrue("Data import was not successfull.", report.isSuccess());

		return instanceReader.getInstances();

	}
}
