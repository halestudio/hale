/*
 * Copyright (c) 2016 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.jdbc.mssql;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.URIBuilder;

/**
 * Create JDBC Uri for MS SQL database
 * 
 * @author Arun
 */
public class MsSqlURIBuilder implements URIBuilder {

	private static final ALogger log = ALoggerFactory.getLogger(MsSqlURIBuilder.class);
	private static final String ENC = "UTF-8";

	private static final String DRIVER = "jdbc:sqlserver://";

	@Override
	public URI createJdbcUri(String host, String database) {
		if (host == null || host.isEmpty()) {
			throw new IllegalArgumentException("A host name must be provided");
		}

		if (database == null || database.isEmpty())
			throw new IllegalArgumentException("A database name must be provided");
		return URI.create(DRIVER + getEncodedHost(host) + ";databaseName=" + database);
	}

	private String getEncodedHost(String host) {
		try {
			return URLEncoder.encode(host, ENC);
		} catch (UnsupportedEncodingException e) {
			log.error(ENC + "! that's supposed to be an encoding", e);
			return host;
		}
	}

}
