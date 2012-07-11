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

import eu.esdihumboldt.hale.io.jdbc.extension.internal.ConnectionConfigurerExtension;

/**
 * Helper class that should be used to create JDBC connections, as it
 * includes database specific configuration provided through extensions.
 * @author Simon Templer
 */
public abstract class JDBCConnection {
	
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

}
