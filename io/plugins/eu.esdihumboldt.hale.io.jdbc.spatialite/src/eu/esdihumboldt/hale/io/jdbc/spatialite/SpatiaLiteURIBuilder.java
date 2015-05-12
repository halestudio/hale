package eu.esdihumboldt.hale.io.jdbc.spatialite;

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

		return jdbcUri.toString().substring(PREFIX.length());
	}

}
