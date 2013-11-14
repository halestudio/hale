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

package eu.esdihumboldt.hale.io.jdbc.extension;

import java.net.URI;

/**
 * Builds a JDBC URI from basic database information.
 * 
 * @author Simon Templer
 */
public interface URIBuilder {

	/**
	 * Create an URI to connect to a specific database via JDBC.
	 * 
	 * @param host the database host (and port)
	 * @param database the database name
	 * @return the JDBC URI
	 */
	public URI createJdbcUri(String host, String database);

}
