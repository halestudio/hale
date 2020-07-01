package eu.esdihumboldt.hale.io.jdbc.spatialite;

import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConnection;
import org.sqlite.core.DB;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

/**
 * 
 * Enables SpatiaLite extension on the given {@link SQLiteConnection}. If native
 * SpatiaLite library is not found, an exception is logged.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteConnectionConfigurer implements ConnectionConfigurer<SQLiteConnection> {

	private static final ALogger log = ALoggerFactory
			.getLogger(SpatiaLiteConnectionConfigurer.class);

	/**
	 * Enable SpatiaLite extension for the provided connection.
	 */
	@Override
	public void configureConnection(SQLiteConnection connection) {
		DB sqliteDB = connection.getDatabase();
		Statement stmt = null;
		try {
			sqliteDB.enable_load_extension(true);

			stmt = connection.createStatement();
			stmt.setQueryTimeout(30); // set timeout to 30 sec.

			// loading SpatiaLite
			stmt.execute("SELECT load_extension('mod_spatialite')");
		} catch (SQLException e) {
			// just a warning - maybe only SQLite is needed
			log.warn(
					"Failed to load SpatiaLite extension (mod_spatialite). Please check the help on how to make it available.",
					e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}
	}

}
