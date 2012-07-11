/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
 * Helper class that should be used to create JDBC connections, as it
 * includes database specific configuration provided through extensions.
 * @author Simon Templer
 */
public abstract class JDBCConnection implements JDBCConstants {
	
	/**
	 * Get a connection to a database.
	 * @param jdbcUri the JDBC URI to access the database
	 * @param user the user name
	 * @param password the password
	 * @return the database connection
	 * @throws SQLException if establishing the connection fails
	 */
	public static Connection getConnection(URI jdbcUri, String user, 
			String password) throws SQLException {
		Connection connection = DriverManager.getConnection(jdbcUri.toString(),
				user, password);
		
		// do database specific configuration
		ConnectionConfigurerExtension.getInstance().applyAll(connection);
		
		return connection;
	}
	
	/**
	 * Get a connection to a database, as configured in the given
	 * import provider.
	 * @param jdbcImportProvider the import provider
	 * @return the database connection
	 * @throws SQLException if establishing the connection fails
	 */
	public static Connection getConnection(ImportProvider jdbcImportProvider) throws SQLException {
		return getConnection(jdbcImportProvider.getSource().getLocation(), jdbcImportProvider);
	}
	
	/**
	 * Get a connection to a database, as configured in the given
	 * export provider.
	 * @param jdbcExportProvider the export provider
	 * @return the database connection
	 * @throws SQLException if establishing the connection fails
	 */
	public static Connection getConnection(ExportProvider jdbcExportProvider) throws SQLException {
		return getConnection(jdbcExportProvider.getTarget().getLocation(), jdbcExportProvider);
	}
	
	@SuppressWarnings("null")
	private static Connection getConnection(URI jdbcURI, IOProvider jdbcIOProvider) throws SQLException {
		Preconditions.checkArgument(jdbcURI != null, "JDBC URI needed");
		Preconditions.checkArgument(jdbcURI.toString().startsWith("jdbc:"), "Invalid JDBC URI");
		
		String user = jdbcIOProvider.getParameter(PARAM_USER);
		String password = jdbcIOProvider.getParameter(PARAM_PASSWORD);
		
		// connect to the database
		return JDBCConnection.getConnection(jdbcURI, user, password);
	}

}
