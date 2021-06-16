package eu.esdihumboldt.hale.io.jdbc.mysql;

import java.net.URI;

import eu.esdihumboldt.hale.io.jdbc.extension.URIBuilder;

public class MySqlUriBuilder implements URIBuilder {

	@Override
	public URI createJdbcUri(String host, String database) {
	
		if (host == null || host.isEmpty()) {
			throw new IllegalArgumentException("A host name must be provided");
		}

		if (database == null || database.isEmpty()) {
			throw new IllegalArgumentException("A database name must be provided");
		}

		return URI.create("jdbc:mysql://" + host + "/" + database);
	}
}
