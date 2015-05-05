package eu.esdihumboldt.hale.io.jdbc.spatialite;

import java.net.URI;

import eu.esdihumboldt.hale.io.jdbc.extension.URIBuilder;

public class SpatiaLiteURIBuilder implements URIBuilder {

	/**
	 * {@code host} parameter is ignored.
	 */
	@Override
	public URI createJdbcUri(String host, String database) {
		if (database == null || database.isEmpty()) {
			throw new IllegalArgumentException("A database name must be provided");
		}

		return URI.create("jdbc:sqlite:" + database);
	}

}
