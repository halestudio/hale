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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.sqlite.SQLiteConfig;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

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
import eu.esdihumboldt.hale.io.jdbc.spatialite.reader.internal.SpatiaLiteInstanceReader;
import eu.esdihumboldt.hale.io.jdbc.spatialite.reader.internal.SpatiaLiteSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.spatialite.writer.internal.SpatiaLiteInstanceWriter;

/**
 * Utility class with shared constants and methods used by SpatiaLite test
 * classes.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteTestUtil {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteTestUtil.class);

	private static final String WKT_POLYGON = "POLYGON((146.232727 -42.157501, 146.238007 -42.16111, "
			+ "146.24411 -42.169724, 146.257202 -42.193329, 146.272217 -42.209442, "
			+ "146.274689 -42.214165, 146.27832 -42.21833, 146.282471 -42.228882, "
			+ "146.282745 -42.241943, 146.291351 -42.255836, 146.290253 -42.261948, "
			+ "146.288025 -42.267502, 146.282471 -42.269997, 146.274994 -42.271111, "
			+ "146.266663 -42.270279, 146.251373 -42.262505, 146.246918 -42.258057, "
			+ "146.241333 -42.256111, 146.23468 -42.257782, 146.221344 -42.269165, "
			+ "146.210785 -42.274445, 146.20163 -42.27417, 146.196075 -42.271385, "
			+ "146.186646 -42.258057, 146.188568 -42.252785, 146.193298 -42.249443, "
			+ "146.200806 -42.248055, 146.209137 -42.249168, 146.217468 -42.248611, "
			+ "146.222473 -42.245277, 146.22525 -42.240555, 146.224121 -42.22805, "
			+ "146.224396 -42.221382, 146.228302 -42.217216, 146.231354 -42.212502, "
			+ "146.231628 -42.205559, 146.219421 -42.186943, 146.21637 -42.17028, "
			+ "146.216644 -42.16333, 146.219696 -42.158607, 146.225525 -42.156105, "
			+ "146.232727 -42.157501))";
	private static Geometry POLYGON = null;

	static {
		try {
			POLYGON = new WKTReader().read(WKT_POLYGON);
		} catch (ParseException e) {
			// should never happen
		}
	}

	/**
	 * File name of the source database.
	 */
	public static final String SOURCE_DB_NAME = "tasmania_water_bodies.sqlite";
	/**
	 * Resource name of the source database.
	 */
	public static final String SOURCE_DB_LOCATION = "/data/" + SOURCE_DB_NAME;
	/**
	 * Unqualified name of the only type stored in the source database.
	 */
	public static final String SOUURCE_TYPE_LOCAL_NAME = "tasmania_water_bodies";

	/**
	 * ID property name.
	 */
	public static final String PROPERTY_ID_NAME = "PK_UID";
	/**
	 * ID of the only instance that will be checked by tests.
	 */
	public static final Integer PROPERTY_ID_VALUE = 1;

	/**
	 * Property names of the source type.
	 */
	public static final String[] SOUURCE_TYPE_PROPERTY_NAMES = new String[] { "AREA", "CNTRY_NAME",
			"CONTINENT", "Geometry", "PERIMETER", PROPERTY_ID_NAME, "WATER_TYPE" };
	/**
	 * Property values of the only instance that will be checked by tests.
	 */
	public static final Object[] SOUURCE_TYPE_PROPERTY_VALUES = new Object[] { 1064866676,
			"Australia", "Australia", POLYGON, 1071221047, 1, "Lake" };
	/**
	 * Number of instances in the source database.
	 */
	public static final int SOURCE_INSTANCES_COUNT = 7;

	/**
	 * File name of the target database.
	 */
	public static final String TARGET_DB_NAME = "tasmania_water_bodies_target.sqlite";

	private static Long RANDOM_NUMBER = null;

	/**
	 * Copies the source database to a temporary file.
	 * 
	 * @throws IOException if temp file can't be created
	 */
	public static void createSourceTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(SpatiaLiteTestUtil.class
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
	public static void createTargetTempFile() throws IOException {
		ByteSource source = Resources.asByteSource(SpatiaLiteTestUtil.class
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
	public static void deleteSourceTempFile() {
		deleteTempFile(getSourceTempFilePath());
	}

	/**
	 * Deletes the target temp file.
	 */
	public static void deleteTargetTempFile() {
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
	public static String getSourceTempFilePath() {
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
	public static String getTargetTempFilePath() {
		return getTempDir() + File.separator + getRandomNumber() + "_" + TARGET_DB_NAME;
	}

	private static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

	private static long getRandomNumber() {
		if (RANDOM_NUMBER == null) {
			RANDOM_NUMBER = System.currentTimeMillis();
		}
		return RANDOM_NUMBER;
	}

	private static void deleteTempFile(String tempFilePath) {
		File toBeDeleted = new File(tempFilePath);
		if (toBeDeleted.exists()) {
			toBeDeleted.delete();
		}
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
	public static boolean isSpatiaLiteExtensionAvailable() {
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
	public static Schema readSchema(String sourceFilePath) throws Exception {

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
	public static InstanceCollection readInstances(Schema sourceSchema, String sourceFilePath)
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
	 * @param targetFilePath the path to the target database file
	 * @param instances the instances to write
	 * @throws Exception any exception thrown by
	 *             {@link SpatiaLiteInstanceWriter}
	 */
	public static void writeInstances(String targetFilePath, InstanceCollection instances)
			throws Exception {

		SpatiaLiteInstanceWriter instanceWriter = new SpatiaLiteInstanceWriter();
		instanceWriter.setInstances(instances);
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
	public static void checkInstances(InstanceCollection instances, Map<String, Object> propertyMap) {
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
	public static void checkType(TypeDefinition type, String typeName, Set<String> propertyNames) {
		assertNotNull(type);
		assertEquals(typeName, type.getDisplayName());

		for (ChildDefinition<?> child : type.getChildren()) {
			assertTrue(propertyNames.contains(child.getName().getLocalPart()));
		}
	}

}
