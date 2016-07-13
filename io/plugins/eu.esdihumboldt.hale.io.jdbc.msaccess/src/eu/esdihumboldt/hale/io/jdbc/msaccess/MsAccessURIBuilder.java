package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.net.URI;

import eu.esdihumboldt.hale.io.jdbc.extension.URIBuilder;

/**
 * Create JDBC URIs for Ms Access database.
 * 
 * @author Arun Varma
 */
public class MsAccessURIBuilder implements URIBuilder {

	private static final String DRIVER = "jdbc:ucanaccess://";

	private static final String PARAM = ";showschema=true;sysschema=true";

	@Override
	public URI createJdbcUri(String host, String databaseFile) {
		if (databaseFile == null || databaseFile.isEmpty())
			throw new IllegalArgumentException("A database file must be provided");
		return URI.create(DRIVER + databaseFile + PARAM);
	}

	/**
	 * @param jdbcUri the JDBC URI
	 * @return the file system path to the SpatiaLite database
	 */
	public static String getDatabase(URI jdbcUri) {
		if (jdbcUri == null) {
			throw new IllegalArgumentException("JDBC URI must be provided");
		}

		return jdbcUri.toString().substring(DRIVER.length());
	}

}
