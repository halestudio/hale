package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.URIBuilder;

/**
 * Create JDBC URIs for Ms Access database.
 * 
 * @author Arun Varma
 */
public class MsAccessURIBuilder implements URIBuilder {

	private static final ALogger log = ALoggerFactory.getLogger(MsAccessURIBuilder.class);
	private static final String ENC = "UTF-8";
	// UCanAccess Driver;
	private static final String DRIVER = "jdbc:ucanaccess://";

	// UCanAccess Driver properties. Property : singleconnection to release the
	// connection after closing it.
	private static final String PARAM = ";showschema=true;sysschema=false;singleconnection=true";

	@Override
	public URI createJdbcUri(String host, String database) {
		if (database == null || database.isEmpty())
			throw new IllegalArgumentException("A database file must be provided");
		String rawDatabasePath = getEncodedPath(new File(database).toURI().getPath());
		return URI.create(DRIVER + rawDatabasePath + PARAM);
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

	private String getEncodedPath(String path) {
		try {
			return URLEncoder.encode(path, ENC);
		} catch (UnsupportedEncodingException e) {
			log.error(ENC + "! that's supposed to be an encoding", e);
			return path;
		}
	}

}
