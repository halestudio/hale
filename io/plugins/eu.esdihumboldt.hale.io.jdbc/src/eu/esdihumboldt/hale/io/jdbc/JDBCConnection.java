/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.common.base.Preconditions;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.io.jdbc.extension.internal.ConnectionConfigurerExtension;

/**
 * Helper class that should be used to create JDBC connections, as it includes
 * database specific configuration provided through extensions.
 * 
 * @author Simon Templer
 */
public abstract class JDBCConnection implements JDBCConstants {

	/**
	 * Get a connection to a database.
	 * 
	 * @param jdbcUri the JDBC URI to access the database
	 * @param user the user name
	 * @param password the password
	 * @return the database connection
	 * @throws SQLException if establishing the connection fails
	 */
	public static Connection getConnection(URI jdbcUri, String user, String password)
			throws SQLException {
		Connection connection = DriverManager.getConnection(jdbcUri.toString(), user, password);

		// do database specific configuration
		ConnectionConfigurerExtension.getInstance().applyAll(connection);

		return connection;
	}

	/**
	 * Get a connection to a database, as configured in the given import
	 * provider.
	 * 
	 * @param jdbcImportProvider the import provider
	 * @return the database connection
	 * @throws SQLException if establishing the connection fails
	 */
	public static Connection getConnection(ImportProvider jdbcImportProvider) throws SQLException {
		return getConnection(jdbcImportProvider.getSource().getLocation(), jdbcImportProvider);
	}

	/**
	 * Get a connection to a database, as configured in the given export
	 * provider.
	 * 
	 * @param jdbcExportProvider the export provider
	 * @return the database connection
	 * @throws SQLException if establishing the connection fails
	 */
	public static Connection getConnection(ExportProvider jdbcExportProvider) throws SQLException {
		return getConnection(jdbcExportProvider.getTarget().getLocation(), jdbcExportProvider);
	}

	private static Connection getConnection(URI jdbcURI, IOProvider jdbcIOProvider)
			throws SQLException {
		Preconditions.checkArgument(jdbcURI != null, "JDBC URI needed");
		Preconditions.checkArgument(jdbcURI.toString().startsWith("jdbc:"), "Invalid JDBC URI");

		String user = jdbcIOProvider.getParameter(PARAM_USER).as(String.class);
		String password = jdbcIOProvider.getParameter(PARAM_PASSWORD).as(String.class);

		// connect to the database
		return JDBCConnection.getConnection(jdbcURI, user, password);
	}

}
