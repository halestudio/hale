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

package eu.esdihumboldt.hale.io.jdbc.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import com.spotify.docker.client.DockerException;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.NoStreamInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.NoStreamOutputSupplier;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceUtil;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;
import eu.esdihumboldt.hale.io.jdbc.JDBCConnection;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceWriter;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;

/**
 * Base class for database tests.
 * 
 * @author Simon Templer
 */
public abstract class AbstractDBTest {

	/**
	 * the config key specifying the time in seconds required for starting the
	 * database
	 */
	private final DBImageParameters dbi;
	private DBDockerClient client;
	private URI jdbcUri;

	/**
	 * @param imageParams
	 * 
	 */
	public AbstractDBTest(DBImageParameters imageParams) {
		this.dbi = imageParams;

	}

	/**
	 * Setup host and database.
	 * 
	 * @throws InterruptedException
	 * @throws DockerException
	 */
	@Before
	public void setupDB() throws DockerException, InterruptedException {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		try {
			// set context class loaded to this class' class loader to be able
			// to find hale-docker.conf
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			client = new DBDockerClient(dbi);
			client.createContainer();
			client.startContainer();

		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}

		jdbcUri = URI.create(dbi.getJDBCURL(client.getHostPort(dbi.getDBPort()),
				client.getHostName()));

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
		int waitTime = 240;
		SQLException lastException = null;
		Connection result = null;

		waitTime = dbi.getStartUPTime();

		while (num < waitTime) {
			try {
				result = JDBCConnection.getConnection(jdbcUri, dbi.getUser(), dbi.getPassword());
				break;
			} catch (SQLException e) {
				// if (!e.getMessage().toLowerCase().contains("database")) {
				// throw e;
				// }
				lastException = e;
			}

			num++;
			System.out.print(num + " ");

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

		schemaReader.setSource(new NoStreamInputSupplier(jdbcUri));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_USER, Value.of(dbi.getUser()));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_PASSWORD, Value.of(dbi.getPassword()));

		// This is set for setting inclusion rule for reading schema
		if (dbi.getDatabase().equalsIgnoreCase("ORCL")) {

			schemaReader.setParameter(JDBCSchemaReader.SCHEMAS, Value.of("SIMON"));
		}
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
		writer.setTarget(new NoStreamOutputSupplier(jdbcUri));
		writer.setParameter(JDBCInstanceWriter.PARAM_USER, Value.of(dbi.getUser()));
		writer.setParameter(JDBCInstanceWriter.PARAM_PASSWORD, Value.of(dbi.getPassword()));
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
		reader.setSource(new NoStreamInputSupplier(jdbcUri));
		reader.setParameter(JDBCInstanceWriter.PARAM_USER, Value.of(dbi.getUser()));
		reader.setParameter(JDBCInstanceWriter.PARAM_PASSWORD, Value.of(dbi.getPassword()));
		DefaultSchemaSpace sourceSchema = new DefaultSchemaSpace();
		sourceSchema.addSchema(schema);
		reader.setSourceSchema(sourceSchema);
		IOReport report = reader.execute(null);
		assertTrue(report.isSuccess());
		assertTrue(report.getErrors().isEmpty());
		return reader.getInstances();
	}

	/**
	 * 
	 * @param originalInstances instance created and written to db
	 * @param schema schema read
	 * @param gType the geometry type
	 * @return read the instances from the db, check if it is same as written db
	 *         and return the count
	 * @throws Exception
	 */

	protected int readAndCountInstances(InstanceCollection originalInstances, Schema schema,
			TypeDefinition gType) throws Exception {

		InstanceCollection instancesRead = readInstances(schema).select(new TypeFilter(gType));
		List<Instance> originals = new DefaultInstanceCollection(originalInstances).toList();

		ResourceIterator<Instance> ri = instancesRead.iterator();
		int count = 0;
		try {
			while (ri.hasNext()) {
				Instance instance = ri.next();

				String error = InstanceUtil.checkInstance(instance, originals);

				assertNull(error, error);
				count++;
			}
		} finally {
			ri.close();
		}

		return count;
	}

	/**
	 * Shutdown database and host.
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDownDocker() throws Exception {
		client.killAndRemoveContainer();
	}

}
