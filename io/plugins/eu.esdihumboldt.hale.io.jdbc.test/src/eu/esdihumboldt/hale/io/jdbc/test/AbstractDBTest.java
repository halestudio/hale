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

import com.typesafe.config.Config;

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

	public static final String DB_UPTIME = ".dbUPTime";
	private DBImageParameters dbi;
	private DBDockerClient tdc;
	public static final String ORACLE_DB = "ORCL";

	/**
	 * Setup host and database.
	 */
	public void setupDB(DBImageParameters dbi) {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			// set context class loaded to this class' class loader to be able
			// to find docker.conf
			Thread.currentThread().setContextClassLoader(
					getClass().getClassLoader());
			tdc = new DBDockerClient(dbi.getHost());
			tdc.startContainer(dbi.isPriviliged());

		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		} 

		int port = tdc.getPort(dbi.getPort());
		String hostName = tdc.getHostName();

		URI databaseUri = URI.create(dbi.getStartUrl() + hostName + ":" + port
				+ "/" + dbi.getDatabase());

		dbi.setDatabaseUri(databaseUri);
		this.dbi = dbi;

	}

	/**
	 * Wait for the database to be ready.
	 * 
	 * @throws SQLException
	 *             if connecting to the database fails
	 */
	protected void waitForDatabase() throws SQLException {
		waitForConnection().close();
	}

	/**
	 * Wait for connection to database.
	 * 
	 * @return the connection to the database once it is set up, the caller is
	 *         responsible to close it
	 * @throws SQLException
	 *             if connecting to the database fails
	 */
	protected Connection waitForConnection() throws SQLException {
		int num = 0;
		int waitTime = 240;
		SQLException lastException = null;
		Connection result = null;
		Config conf = tdc.getConfig();

		if (conf.hasPath(dbi.getHost() + DB_UPTIME)) {
			waitTime = conf.getInt(dbi.getHost() + DB_UPTIME);
		}

		while (num < waitTime) {
			try {
				result = JDBCConnection.getConnection(dbi.getDatabaseUri(),
						dbi.getUser(), dbi.getPassword());
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
	 * @throws Exception
	 *             if reading the schema fails
	 */
	protected Schema readSchema() throws Exception {

		JDBCSchemaReader schemaReader = new JDBCSchemaReader();

		schemaReader.setSource(new NoStreamInputSupplier(dbi.getDatabaseUri()));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_USER,
				Value.of(dbi.getUser()));
		schemaReader.setParameter(JDBCSchemaReader.PARAM_PASSWORD,
				Value.of(dbi.getPassword()));

		// This is set for setting inclusion rule for reading schema
		if (dbi.getDatabase().equalsIgnoreCase(ORACLE_DB)) {

			schemaReader.setParameter(JDBCSchemaReader.SCHEMAS,
					Value.of("SIMON"/* dbi.getUser() */));
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
	 * @param instances
	 *            the collection of instances
	 * @param schema
	 *            the target schema
	 * @throws Exception
	 *             if writing the instances fails
	 */
	protected void writeInstances(InstanceCollection instances, Schema schema)
			throws Exception {
		JDBCInstanceWriter writer = new JDBCInstanceWriter();
		writer.setTarget(new NoStreamOutputSupplier(dbi.getDatabaseUri()));
		writer.setParameter(JDBCInstanceWriter.PARAM_USER,
				Value.of(dbi.getUser()));
		writer.setParameter(JDBCInstanceWriter.PARAM_PASSWORD,
				Value.of(dbi.getPassword()));
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
	 * @param schema
	 *            the source schema
	 * @return the database instances
	 * 
	 * @throws Exception
	 *             if reading the instances fails
	 */
	protected InstanceCollection readInstances(Schema schema)
			throws Exception {
		JDBCInstanceReader reader = new JDBCInstanceReader();
		reader.setSource(new NoStreamInputSupplier(dbi.getDatabaseUri()));
		reader.setParameter(JDBCInstanceWriter.PARAM_USER,
				Value.of(dbi.getUser()));
		reader.setParameter(JDBCInstanceWriter.PARAM_PASSWORD,
				Value.of(dbi.getPassword()));
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
 * @return read the instances from the db, check if it is same as written db and return the count
 * @throws Exception
 */

	protected int readAndCountInstances(InstanceCollection originalInstances, Schema schema,
			TypeDefinition gType) throws Exception {

		InstanceCollection instancesRead = readInstances(schema).select(new TypeFilter(gType));
		List<Instance> originals = new DefaultInstanceCollection(originalInstances)
				.toList();
		
		ResourceIterator<Instance> ri = instancesRead.iterator();
		int count = 0;
		try {
			while (ri.hasNext()) {
				Instance instance = (Instance) ri.next();

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
	 */
	public void tearDownDocker() {
		tdc.killAndRemoveContainer();
	}

}
