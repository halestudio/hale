package eu.esdihumboldt.hale.io.jdbc.mssql.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;

import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.NoStreamInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.mssql.MsSqlURIBuilder;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test class for Ms SQL Server
 * 
 * @author Arun
 *
 */
@Features("Databases")
@Stories("SQL Server")
public class MsSqlDataReaderTest {

	/**
	 * Source Database name
	 */
	private URI JDBCURI;

	/**
	 * User name to connect to database
	 */
	private String USER_NAME;

	/**
	 * password to connect to database
	 */
	private String PASSWORD;

	private String HOST;

	private String DATABASE;

	private String SCHEMA;

	private final List<String> tablesInSchema = new ArrayList<>(3);
	private final List<String> tablesNotInSchema = new ArrayList<>(2);

	private final int SOURCE_TOTAL_INSTANCES_COUNT = 11;

	private final int SOURCE_GEOMETRY_INSTANCE_COUNT = 3;

	private String[] SOURCE_GEOMETRY_TYPE_PROPERTY_NAMES;

	private Object[] PROPERTY_ID_VALUES;
	private Object[] PROPERTY_GEO_VALUES;

	/**
	 * Configuration of Junit tests
	 */
	@Before
	public void init() {
		HOST = "WETF-PC001\\SQLEXPRESS";

		DATABASE = "HaleTest";

		USER_NAME = "sa";

		PASSWORD = "123456";

		SCHEMA = "HaleTest.dbo";

		tablesInSchema.add("Department");
		tablesInSchema.add("Employee");
		tablesInSchema.add("SpatialTable");

		tablesNotInSchema.add("TestTable");

		SOURCE_GEOMETRY_TYPE_PROPERTY_NAMES = new String[] { "id", "GeomCol1" };
		PROPERTY_ID_VALUES = new Object[] { 1, 2, 3 };

		PROPERTY_GEO_VALUES = new Object[] { "LINESTRING (100 100, 20 180, 180 180)",
				"POLYGON ((0 0, 150 0, 150 150, 0 150, 0 0))", "POINT (3 3)" };

	}

	/**
	 * Test schema reader
	 * 
	 * @throws Exception if error occurred in reading schema
	 */
	@Test
	public void testSchemaReader() throws Exception {
		Schema schema = readSchema();
		assertNotNull(schema);
		Collection<? extends TypeDefinition> k = schema.getMappingRelevantTypes();
		verifyTables(k);
	}

	/**
	 * Test instance reader
	 * 
	 * @throws Exception if error occurred in reading instances
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testInstanceReader() throws Exception {

		// ****** read Schema ******//
		Schema schema = readSchema();

		assertEquals(3, schema.getMappingRelevantTypes().size());

		// ****** read Instances ******//
		InstanceCollection instances = readInstances(schema);
		assertTrue(instances.hasSize());

		assertEquals(SOURCE_TOTAL_INSTANCES_COUNT, instances.size());

		InstanceCollection filteredInstances = null;

		// Check SpatialTable "jdbc:sqlserver:dbo","SpatialTable"
		filteredInstances = instances.select(
				new TypeFilter(schema.getType(new QName("jdbc:sqlserver:dbo", "SpatialTable"))));
		int geometryPropertyCount = 0;
		int idPropertyCount = 0;
		ResourceIterator<Instance> it = filteredInstances.iterator();
		while (it.hasNext()) {
			Instance in = it.next();
			for (String propertyName : SOURCE_GEOMETRY_TYPE_PROPERTY_NAMES) {
				Object value = in.getProperty(QName.valueOf(propertyName))[0];
				if (value == null)
					continue;
				if (value instanceof GeometryProperty) {
					assertTrue(((GeometryProperty<Geometry>) value).getGeometry().toText()
							.equalsIgnoreCase(
									String.valueOf(PROPERTY_GEO_VALUES[geometryPropertyCount])));
					geometryPropertyCount++;

				}
				else {
					assertTrue(((int) value) == ((int) PROPERTY_ID_VALUES[idPropertyCount]));
					idPropertyCount++;
				}
			}
		}

		assertEquals(SOURCE_GEOMETRY_INSTANCE_COUNT, geometryPropertyCount);
		assertEquals(SOURCE_GEOMETRY_INSTANCE_COUNT, idPropertyCount);

	}

	/**
	 * Reads a schema from a MS SQL database.
	 * 
	 * @return the schema
	 * @throws Exception any exception thrown by {@link JDBCSchemaReader}
	 */

	private Schema readSchema() throws Exception {

		JDBCSchemaReader schemaReader = new JDBCSchemaReader();

		JDBCURI = new MsSqlURIBuilder().createJdbcUri(HOST, DATABASE);

		schemaReader.setSource(new NoStreamInputSupplier(JDBCURI));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_USER, Value.of(USER_NAME));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_PASSWORD, Value.of(PASSWORD));

		// This is set for setting inclusion rule for reading schema

		schemaReader.setParameter(JDBCSchemaReader.SCHEMAS, Value.of(SCHEMA));
		IOReport report = schemaReader.execute(new LogProgressIndicator());
		assertTrue(report.isSuccess());
		assertTrue(report.getErrors().isEmpty());
		Schema schema = schemaReader.getSchema();
		assertNotNull(schema);

		return schema;
	}

	/**
	 * Reads instances from from a MsAccess database file with the provided
	 * schema.
	 * 
	 * @param sourceSchema the schema of the source database
	 * @return the read instances
	 * @throws Exception any exception thrown by {@link JDBCInstanceReader}
	 */
	private InstanceCollection readInstances(Schema sourceSchema) throws Exception {

		JDBCInstanceReader instanceReader = new JDBCInstanceReader();

		JDBCURI = new MsSqlURIBuilder().createJdbcUri(HOST, DATABASE);

		instanceReader.setSource(new NoStreamInputSupplier(JDBCURI));
		instanceReader.setSourceSchema(sourceSchema);
		instanceReader.setParameter(JDBCInstanceReader.PARAM_USER, Value.of(USER_NAME));
		instanceReader.setParameter(JDBCInstanceReader.PARAM_PASSWORD, Value.of(PASSWORD));

		// Test instances
		IOReport report = instanceReader.execute(new LogProgressIndicator());
		assertTrue("Data import was not successfull.", report.isSuccess());

		return instanceReader.getInstances();
	}

	private void verifyTables(Collection<? extends TypeDefinition> tables) {
		for (TypeDefinition def : tables) {
			assertTrue(tablesInSchema.contains(def.getDisplayName()));
			assertFalse(tablesNotInSchema.contains(def.getDisplayName()));
		}
	}
}
