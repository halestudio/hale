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
import java.net.URLDecoder;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionHelper;

/**
 * Connection helper for Ms SQL server
 * 
 * @author Arun
 */
public class MsSqlConnectionHelper implements ConnectionHelper {

	private static final ALogger log = ALoggerFactory.getLogger(MsSqlConnectionHelper.class);
	private static final String ENC = "UTF-8";

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.extension.ConnectionHelper#getConnectionString(java.net.URI)
	 */
	@Override
	public String getConnectionString(URI jdbcUri) {
		// need to decode URI prior to establish connection.
		return getDecodedURI(jdbcUri);
	}

	private String getDecodedURI(URI uri) {
		try {
			return URLDecoder.decode(uri.toString(), ENC);
		} catch (UnsupportedEncodingException e) {
			log.error(ENC + "! that's supposed to be an encoding!!", e);
			return uri.toString();
		}
	}

}
