package eu.esdihumboldt.hale.io.jdbc.spatialite.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.sqlite.SQLiteConfig;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.locationtech.jts.geom.Geometry;

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
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.io.jdbc.spatialite.reader.internal.SpatiaLiteInstanceReader;
import eu.esdihumboldt.hale.io.jdbc.spatialite.reader.internal.SpatiaLiteSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.spatialite.writer.internal.SpatiaLiteInstanceWriter;

/**
 * Class exposing methods with test logic, which are run by SpatiaLite test
 * classes.
 * <p>
 * Subclasses should re-define the test parameters (stored in
 * <code>protected</code> variables) to have the test suite run against a
 * different dataset.
 * </p>
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class SpatiaLiteTestSuite {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteTestSuite.class);

	/**
	 * File name of the source database.
	 */
	protected String SOURCE_DB_NAME;

	/**
	 * Resource name of the source database.
	 */
	protected String SOURCE_DB_LOCATION;

	/**
	 * Unqualified name of the only type stored in the source database.
	 */
	protected String SOUURCE_TYPE_LOCAL_NAME;

	/**
	 * ID property name.
	 */
	protected String PROPERTY_ID_NAME;

	/**
	 * ID of the only instance that will be checked by tests.
	 */
	protected Integer PROPERTY_ID_VALUE;

	/**
	 * Property names of the source type.
	 */
	protected String[] SOUURCE_TYPE_PROPERTY_NAMES;

	/**
	 * Property values of the only instance that will be checked by tests.
	 */
	protected Object[] SOUURCE_TYPE_PROPERTY_VALUES;

	/**
	 * Number of instances in the source database.
	 */
	protected int SOURCE_INSTANCES_COUNT;

	/**
	 * File name of the target database.
	 */
	protected String TARGET_DB_NAME;

	private static Long RANDOM_NUMBER = null;

	/**
	 * Copies the source database to a temporary file.
	 * 
	 * @throws IOException if temp file can't be created
	 */
	public void createSourceTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(SpatiaLiteTestSuite.class
				.getResource(SOURCE_DB_LOCATION));
		ByteSink dest = Files.asByteSink(new File(getSourceTempFilePath()));

		source.copyTo(dest);
	}

	/**
	 * Copies the target database to a temporary file and deletes all instances
	 * in it.
	 * 
	 * @throws IOException if temp file can't be created or instances can't be
	 *             deleted
	 */
	public void createTargetTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(SpatiaLiteTestSuite.class
				.getResource(SOURCE_DB_LOCATION));
		ByteSink dest = Files.asByteSink(new File(getTargetTempFilePath()));

		source.copyTo(dest);

		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + getTargetTempFilePath());

			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DELETE FROM " + SOUURCE_TYPE_LOCAL_NAME);
		} catch (SQLException e) {
			throw new IOException("Could not empty target DB", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}

	}

	/**
	 * Deletes the source temp file.
	 */
	public void deleteSourceTempFile() {
		deleteTempFile(getSourceTempFilePath());
	}

	/**
	 * Deletes the target temp file.
	 */
	public void deleteTargetTempFile() {
		deleteTempFile(getTargetTempFilePath());
	}

	/**
	 * Generates a random path (within the system's temporary folder) for the
	 * source database. The random number used to construct the path is saved in
	 * a static variable and thus the path will remain constant for the whole
	 * run.
	 * 
	 * @return the absolute path of the source temp file
	 */
	public String getSourceTempFilePath() {
		return getTempDir() + File.separator + getRandomNumber() + "_" + SOURCE_DB_NAME;
	}

	/**
	 * Generates a random path (within the system's temporary folder) for the
	 * target database. The random number used to construct the path is saved in
	 * a static variable and thus the path will remain constant for the whole
	 * run.
	 * 
	 * @return the absolute path of the target temp file
	 */
	public String getTargetTempFilePath() {
		return getTempDir() + File.separator + getRandomNumber() + "_" + TARGET_DB_NAME;
	}

	private String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

	private long getRandomNumber() {
		if (RANDOM_NUMBER == null) {
			RANDOM_NUMBER = System.currentTimeMillis();
		}
		return RANDOM_NUMBER;
	}

	private void deleteTempFile(String tempFilePath) {
		File toBeDeleted = new File(tempFilePath);
		if (toBeDeleted.exists()) {
			toBeDeleted.delete();
		}
	}

	/**
	 * Test - reads a sample SpatiaLite schema
	 * 
	 * @throws Exception if an error occurs
	 */
	public void schemaReaderTest() throws Exception {
		if (!isSpatiaLiteExtensionAvailable()) {
			log.info("Skipping test because SpatiaLite extension is not available");
			return;
		}

		Set<String> propertyNames = new HashSet<String>(Arrays.asList(SOUURCE_TYPE_PROPERTY_NAMES));

		SpatiaLiteSchemaReader schemaReader = new SpatiaLiteSchemaReader();
		schemaReader.setSource(new FileIOSupplier(new File(getSourceTempFilePath())));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();
		assertEquals(1, schema.getMappingRelevantTypes().size());
		TypeDefinition type = schema.getMappingRelevantTypes().iterator().next();

		checkType(type, SOUURCE_TYPE_LOCAL_NAME, propertyNames);
	}

	/**
	 * Test - reads a sample SpatiaLite schema and data.
	 * 
	 * @throws Exception if an error occurs
	 */
	public void instanceReaderTest() throws Exception {
		if (!isSpatiaLiteExtensionAvailable()) {
			log.info("Skipping test because SpatiaLite extension is not available");
			return;
		}

		Map<String, Object> propertyMap = new HashMap<String, Object>();
		for (int i = 0; i < SOUURCE_TYPE_PROPERTY_NAMES.length; i++) {
			String key = SOUURCE_TYPE_PROPERTY_NAMES[i];
			Object value = SOUURCE_TYPE_PROPERTY_VALUES[i];
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
	}

	/**
	 * Test - reads data from a source SpatiaLite database, writes them to a
	 * target SpatiaLite database and checks the results.
	 * 
	 * @throws Exception if an error occurs
	 */
	public void instanceWriterTest() throws Exception {
		if (!isSpatiaLiteExtensionAvailable()) {
			log.info("Skipping test because SpatiaLite extension is not available");
			return;
		}

		Map<String, Object> propertyMap = new HashMap<String, Object>();
		for (int i = 0; i < SOUURCE_TYPE_PROPERTY_NAMES.length; i++) {
			String key = SOUURCE_TYPE_PROPERTY_NAMES[i];
			Object value = SOUURCE_TYPE_PROPERTY_VALUES[i];
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

		writeInstances(schema, getTargetTempFilePath(), instances);

		// re-read instances to check they were written correctly
		targetInstances = readInstances(schema, getTargetTempFilePath());
		assertTrue(targetInstances.hasSize());
		assertEquals(SOURCE_INSTANCES_COUNT, targetInstances.size());

		checkInstances(targetInstances, propertyMap);
	}

	/**
	 * Checks whether the SpatiaLite extension is available on the system, by
	 * connecting to the source database and running the query:
	 * <p>
	 * {@code SELECT load_extension('mod_spatialite')}
	 * </p>
	 * 
	 * @return true if the SpatiaLite extension could be loaded, false otherwise
	 */
	public boolean isSpatiaLiteExtensionAvailable() {
		Connection conn = null;

		try {
			// enabling dynamic extension loading
			// absolutely required by SpatiaLite
			SQLiteConfig config = new SQLiteConfig();
			config.enableLoadExtension(true);

			String dbPath = getSourceTempFilePath();
			if (!new File(dbPath).exists()) {
				createSourceTempFile();
			}

			// create a database connection
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath, config.toProperties());
			Statement stmt = conn.createStatement();
			stmt.setQueryTimeout(30); // set timeout to 30 sec.

			// loading SpatiaLite
			stmt.execute("SELECT load_extension('mod_spatialite')");
		} catch (Exception e) {
			log.warn("Could not load SpatiaLite extension", e);

			return false;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// ignore
			}
		}

		return true;
	}

	/**
	 * Reads a schema from a SpatiaLite database file.
	 * 
	 * @param sourceFilePath the path to the source database file
	 * @return the schema
	 * @throws Exception any exception thrown by {@link SpatiaLiteSchemaReader}
	 */
	public Schema readSchema(String sourceFilePath) throws Exception {

		SpatiaLiteSchemaReader schemaReader = new SpatiaLiteSchemaReader();
		schemaReader.setSource(new FileIOSupplier(new File(sourceFilePath)));

		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());

		return schemaReader.getSchema();

	}

	/**
	 * Reads instances from from a SpatiaLite database file with the provided
	 * schema.
	 * 
	 * @param sourceSchema the schema of the source database
	 * @param sourceFilePath the path to the source database file
	 * @return the read instances
	 * @throws Exception any exception thrown by
	 *             {@link SpatiaLiteInstanceReader}
	 */
	public InstanceCollection readInstances(Schema sourceSchema, String sourceFilePath)
			throws Exception {

		SpatiaLiteInstanceReader instanceReader = new SpatiaLiteInstanceReader();
		instanceReader.setSource(new FileIOSupplier(new File(sourceFilePath)));
		instanceReader.setSourceSchema(sourceSchema);

		// Test instances
		IOReport report = instanceReader.execute(new LogProgressIndicator());
		assertTrue("Data import was not successfull.", report.isSuccess());

		return instanceReader.getInstances();

	}

	/**
	 * Writes the provided instances to a SpatiaLite database.
	 * 
	 * @param schema the target schema
	 * @param targetFilePath the path to the target database file
	 * @param instances the instances to write
	 * @throws Exception any exception thrown by
	 *             {@link SpatiaLiteInstanceWriter}
	 */
	public void writeInstances(Schema schema, String targetFilePath, InstanceCollection instances)
			throws Exception {

		SpatiaLiteInstanceWriter instanceWriter = new SpatiaLiteInstanceWriter();
		instanceWriter.setInstances(instances);
		DefaultSchemaSpace ss = new DefaultSchemaSpace();
		ss.addSchema(schema);
		instanceWriter.setTargetSchema(ss);
		instanceWriter.setTarget(new FileIOSupplier(new File(targetFilePath)));

		// Test instances
		IOReport report = instanceWriter.execute(new LogProgressIndicator());
		assertTrue("Data export was not successfull.", report.isSuccess());

	}

	/**
	 * Checks the property definitions and values of the provided instances.
	 * Values will be checked for just one instance (the one with
	 * {@link #PROPERTY_ID_NAME} = {@link #PROPERTY_ID_VALUE}).
	 * 
	 * @param instances the instances to check
	 * @param propertyMap the expected property names / values
	 */
	@SuppressWarnings("rawtypes")
	public void checkInstances(InstanceCollection instances, Map<String, Object> propertyMap) {
		// get type to check property definition
		TypeDefinition type = instances.iterator().next().getDefinition();
		checkType(type, SOUURCE_TYPE_LOCAL_NAME, propertyMap.keySet());

		// Check the values of Instance with ID = 1
		Instance instance = null;
		Iterator<Instance> instanceIterator = instances.iterator();
		while (instanceIterator.hasNext()) {
			Instance currentInstance = instanceIterator.next();
			Integer id = (Integer) currentInstance.getProperty(QName.valueOf(PROPERTY_ID_NAME))[0];
			if (PROPERTY_ID_VALUE.equals(id)) {
				instance = currentInstance;
				break;
			}
		}

		if (instance == null) {
			fail(String.format("No instance found with %s = %s", PROPERTY_ID_NAME,
					PROPERTY_ID_VALUE));
		}

		for (String propertyName : propertyMap.keySet()) {
			@SuppressWarnings("null")
			Object value = instance.getProperty(QName.valueOf(propertyName))[0];
			if (value instanceof GeometryProperty) {
				assertTrue(((Geometry) propertyMap.get(propertyName)).equalsExact(
						((GeometryProperty) value).getGeometry(), 0.000001));
			}
			else {
				assertEquals(propertyMap.get(propertyName), value);
			}
		}
	}

	/**
	 * Checks the name and the property definitions of the given type.
	 * 
	 * @param type the type to check
	 * @param typeName the expected type name
	 * @param propertyNames the expected property names
	 */
	public void checkType(TypeDefinition type, String typeName, Set<String> propertyNames) {
		assertNotNull(type);
		assertEquals(typeName, type.getDisplayName());

		for (ChildDefinition<?> child : type.getChildren()) {
			assertTrue(propertyNames.contains(child.getName().getLocalPart()));
		}
	}

}
