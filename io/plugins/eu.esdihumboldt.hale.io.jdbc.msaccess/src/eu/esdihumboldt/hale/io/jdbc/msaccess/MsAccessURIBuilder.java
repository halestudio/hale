package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;

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
	private static final String UTF8 = "UTF-8";
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
	 * @param uri the JDBC URI or a file URI pointing to the database file
	 * @return the file system path to the MsAccess database
	 */
	public static String getDatabase(URI uri) {
		if (uri == null) {
			throw new IllegalArgumentException("MS Access JDBC URI must be provided");
		}

		if ("jdbc".equals(uri.getScheme())) {
			String encodedPath = uri.toString().substring(DRIVER.length());
			if (encodedPath.contains(";")) {
				encodedPath = encodedPath.substring(0, encodedPath.indexOf(';'));
			}

			try {
				return URLDecoder.decode(encodedPath, UTF8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						MessageFormat.format("Encoding {0} is not supported", UTF8));
			}
		}
		else if ("file".equals(uri.getScheme())) {
			return new File(uri).getAbsolutePath();
		}
		else {
			throw new IllegalArgumentException(MessageFormat.format(
					"Unsupported source URI scheme \"{0}\". Only \"file\" and \"jdbc\" allowed",
					uri));
		}

	}

	private String getEncodedPath(String path) {
		try {
			return URLEncoder.encode(path, UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(MessageFormat.format("Encoding {0} is not supported", UTF8));
		}
	}
}
