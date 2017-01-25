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

package eu.esdihumboldt.hale.io.jdbc.mssql.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.NoStreamInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;
import eu.esdihumboldt.hale.io.jdbc.mssql.MsSqlURIBuilder;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test with another instance of sql server
 * 
 * @author Arun
 */
@Features("Databases")
@Stories("SQL Server")
public class MsSQLServer14Test {

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

	/**
	 * Configuration of Junit tests
	 */
	@Before
	public void init() {
		HOST = "localhost\\SQLEXPRESS14";

		DATABASE = "MUDAB_Test";

		USER_NAME = "sa";

		PASSWORD = "123456";

		SCHEMA = "MUDAB_Test.dbo,MUDAB_Test.test";

	}

	/**
	 * Test schema reader
	 * 
	 * @throws Exception if error occurred in reading schema
	 */
	@Test
	public void testAschemaReader() throws Exception {
		Schema schema = readSchema();
		assertNotNull(schema);
	}

	/**
	 * Test instance reader
	 * 
	 * @throws Exception if error occurred in reading instances
	 */
	@Test
	public void testInstanceReader() throws Exception {

		// ****** read Schema ******//
		Schema schema = readSchema();

		// ****** read Instances ******//
		InstanceCollection instances = readInstances(schema);
		assertTrue(instances.hasSize());
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

}
