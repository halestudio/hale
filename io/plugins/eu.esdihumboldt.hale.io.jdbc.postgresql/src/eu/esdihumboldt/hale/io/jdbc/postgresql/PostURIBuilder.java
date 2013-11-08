/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc.postgresql;

import java.net.URI;

import eu.esdihumboldt.hale.io.jdbc.extension.URIBuilder;

/**
 * Create JDBC URIs for postgresql/PostGIS.
 * 
 * @author Simon Templer
 */
public class PostURIBuilder implements URIBuilder {

	@Override
	public URI createJdbcUri(String host, String database) {
		if (host == null || host.isEmpty()) {
			throw new IllegalArgumentException("A host name must be provided");
		}

		if (database == null || database.isEmpty()) {
			throw new IllegalArgumentException("A database name must be provided");
		}

		return URI.create("jdbc:postgresql://" + host + "/" + database);
	}

}
