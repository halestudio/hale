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

package eu.esdihumboldt.hale.io.jdbc.postgresql.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.NoStreamInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.NoStreamOutputSupplier;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.jdbc.JDBCConnection;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceWriter;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;

/**
 * Base class for PostgreSQL database tests.
 * 
 * @author Simon Templer
 */
public abstract class AbstractDBTest {

	/**
	 * The database user name
	 */
	public static final String USER = "docker";
	/**
	 * The database user password
	 */
	public static final String PASSWORD = "docker";
	/**
	 * The database name
	 */
	public static final String DATABASE = "gis";

	private CloudHost itestHost;

	/**
	 * The database JDBC URI
	 */
	protected URI databaseUri;

	/**
	 * Setup host and database.
	 */
	@Before
	public void setupDB() {
		// start Jersey bundle for OSGi support
		TestUtil.startBundle("com.sun.jersey.core");

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			// set context class loaded to this class' class loader to be able
			// to find overcast.conf
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

			itestHost = CloudHostFactory.getCloudHost("postgis");
			itestHost.setup();
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}

		int port = itestHost.getPort(5432);
		String hostName = itestHost.getHostName();
		databaseUri = URI.create("jdbc:postgresql://" + hostName + ":" + port + "/" + DATABASE);
	}

	/**
	 * Wait for the database to be ready.
	 * 
	 * @throws SQLException if connecting to the database fails
	 */
	protected void waitForDatabase() throws SQLException {
		waitForConnection().close();
	}

	/**
	 * Wait for connection to database.
	 * 
	 * @return the connection to the database once it is set up, the caller is
	 *         responsible to close it
	 * @throws SQLException if connecting to the database fails
	 */
	protected Connection waitForConnection() throws SQLException {
		int num = 0;
		SQLException lastException = null;
		Connection result = null;
		while (num < 240) {
			try {
				result = JDBCConnection.getConnection(databaseUri, USER, PASSWORD);
				break;
			} catch (SQLException e) {
				if (!e.getMessage().toLowerCase().contains("database")) {
					throw e;
				}
				lastException = e;
			}

			num++;
			System.out.print('.');

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		if (num > 0) {
			System.out.println();
		}

		if (result != null) {
			return result;
		}
		if (lastException != null) {
			throw lastException;
		}
		return null;
	}

	/**
	 * Load the database schema.
	 * 
	 * @return the schema
	 * @throws Exception if reading the schema fails
	 */
	protected Schema readSchema() throws Exception {
		JDBCSchemaReader schemaReader = new JDBCSchemaReader();
		schemaReader.setSource(new NoStreamInputSupplier(databaseUri));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_USER, Value.of(USER));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_PASSWORD, Value.of(PASSWORD));
		IOReport report = schemaReader.execute(null);
		assertTrue(report.isSuccess());
		assertTrue(report.getErrors().isEmpty());
		Schema schema = schemaReader.getSchema();
		assertNotNull(schema);
		return schema;
	}

	/**
	 * Write instances to the database.
	 * 
	 * @param instances the collection of instances
	 * @param schema the target schema
	 * @throws Exception if writing the instances fails
	 */
	protected void writeInstances(InstanceCollection instances, Schema schema) throws Exception {
		JDBCInstanceWriter writer = new JDBCInstanceWriter();
		writer.setTarget(new NoStreamOutputSupplier(databaseUri));
		writer.setParameter(JDBCInstanceWriter.PARAM_USER, Value.of(USER));
		writer.setParameter(JDBCInstanceWriter.PARAM_PASSWORD, Value.of(PASSWORD));
		writer.setInstances(instances);
		DefaultSchemaSpace targetSchema = new DefaultSchemaSpace();
		targetSchema.addSchema(schema);
		writer.setTargetSchema(targetSchema);
		IOReport report = writer.execute(null);
		assertTrue(report.isSuccess());
		assertTrue(report.getErrors().isEmpty());
	}

	/**
	 * Read instances from the database.
	 * 
	 * @param schema the source schema
	 * @return the database instances
	 * 
	 * @throws Exception if reading the instances fails
	 */
	protected InstanceCollection readInstances(Schema schema) throws Exception {
		JDBCInstanceReader reader = new JDBCInstanceReader();
		reader.setSource(new NoStreamInputSupplier(databaseUri));
		reader.setParameter(JDBCInstanceWriter.PARAM_USER, Value.of(USER));
		reader.setParameter(JDBCInstanceWriter.PARAM_PASSWORD, Value.of(PASSWORD));
		DefaultSchemaSpace sourceSchema = new DefaultSchemaSpace();
		sourceSchema.addSchema(schema);
		reader.setSourceSchema(sourceSchema);
		IOReport report = reader.execute(null);
		assertTrue(report.isSuccess());
		assertTrue(report.getErrors().isEmpty());
		return reader.getInstances();
	}

	/**
	 * Shutdown database and host.
	 */
	@After
	public void shutdown() {
		itestHost.teardown();
	}

}
