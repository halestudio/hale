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

import org.junit.Test;
import org.postgresql.PGConnection;

import eu.esdihumboldt.hale.io.jdbc.test.AbstractDBTest;
import eu.esdihumboldt.hale.io.jdbc.test.DBConfigInstance;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Basic database tests.
 * 
 * @author Simon Templer
 */
@Features("Databases")
@Stories("PostgreSQL")
public class DatabaseIT extends AbstractDBTest {

	/**
	 * Test the connection.
	 */

	public DatabaseIT() {

		super(new DBConfigInstance("postgis", DatabaseIT.class.getClassLoader()));
	}

	/**
	 * @throws SQLException if the connection cannot be obtained
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

}
