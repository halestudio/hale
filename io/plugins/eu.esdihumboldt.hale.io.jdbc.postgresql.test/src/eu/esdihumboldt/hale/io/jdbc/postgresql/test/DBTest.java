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

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.PGConnection;

import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest;
import eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters;

/**
 * Basic database tests.
 * 
 * @author Simon Templer
 */
public class DBTest extends AbstractDBTest {

	/**
	 * Test the connection.
	 * 
	 * @throws SQLException if the database cannot be accessed
	 */
	@Test
	public void testConnection() throws SQLException {
		Connection connection = waitForConnection();
		try {
			assertTrue(connection instanceof PGConnection);
			assertTrue(connection.createStatement().execute("SELECT 1"));
		} finally {
			connection.close();
		}
	}

	/**
	 * Setup DB, includes create container, start container
	 */
	@Before
	public void setup() {

		DBImageParameters dbi = new DBImageParameters(PostSetup.USER, PostSetup.PASSWORD,
				PostSetup.DATABASE, PostSetup.HOST, PostSetup.START_URL, PostSetup.PORT,
				PostSetup.IS_PRIVILEGED, null);

		setupDB(dbi);
	}

	/**
	 * Kill and remove docker container
	 */
	@After
	public void tearDown() {
		tearDownDocker();
	}
}
