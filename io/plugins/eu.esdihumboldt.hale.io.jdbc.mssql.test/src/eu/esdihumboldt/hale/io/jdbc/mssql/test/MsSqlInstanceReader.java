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

package eu.esdihumboldt.hale.io.jdbc.mssql.test;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader;

/**
 * An extension of {@link JDBCInstanceReader} for MsSQL Server Database
 * 
 * @author Arun
 */
public class MsSqlInstanceReader extends JDBCInstanceReader {

	private static final ALogger log = ALoggerFactory.getLogger(MsSqlInstanceReader.class);
	private static final String ENC = "UTF-8";
	private URI uri;

	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		uri = source.getLocation();
		super.setSource(source);
	}

	@Override
	protected SQLServerConnection getConnection() throws SQLException {

		String user = getParameter(PARAM_USER).as(String.class);
		String password = getParameter(PARAM_PASSWORD).as(String.class);

		SQLServerConnection connection = (SQLServerConnection) DriverManager.getConnection(getDecodedURI(), user, password);
		connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);

		return connection;
	}

	private String getDecodedURI() {
		try {
			return URLDecoder.decode(this.uri.toString(), ENC);
		} catch (UnsupportedEncodingException e) {
			log.error(ENC + "! that's supposed to be an encoding!!", e);
			return this.uri.toString();
		}
	}

}
