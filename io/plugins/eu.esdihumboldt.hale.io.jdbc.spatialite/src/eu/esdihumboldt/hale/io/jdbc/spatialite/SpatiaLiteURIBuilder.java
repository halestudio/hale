package eu.esdihumboldt.hale.io.jdbc.spatialite;

import java.io.File;
import java.net.URI;

import eu.esdihumboldt.hale.io.jdbc.extension.URIBuilder;

/**
 * Create JDBC URIs for SQLite/SpatiaLite.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteURIBuilder implements URIBuilder {

	private static final String PREFIX = "jdbc:sqlite:";

	/**
	 * {@code host} parameter is ignored.
	 */
	@Override
	public URI createJdbcUri(String host, String database) {
		if (database == null || database.isEmpty()) {
			throw new IllegalArgumentException("A database name must be provided");
		}

		// replace system-dependent file separator characters (JDBC URIs must
		// contain forward slash chars only)
		if (File.separatorChar != '/') {
			database = database.replace(File.separatorChar, '/');
		}

		return URI.create(PREFIX + database);
	}

	/**
	 * @param jdbcUri the JDBC URI
	 * @return the file system path to the SpatiaLite database
	 */
	public static String getDatabase(URI jdbcUri) {
		if (jdbcUri == null) {
			throw new IllegalArgumentException("JDBC URI must be provided");
		}

		String database = jdbcUri.toString().substring(PREFIX.length());
		// restores system-dependent file separator characters
		if (File.separatorChar != '/') {
			database.replace('/', File.separatorChar);
		}

		return database;
	}

}
