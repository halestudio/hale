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

package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader;
import net.ucanaccess.jdbc.UcanaccessConnection;

/**
 * Reads instances from MsAccess Database
 * 
 * @author Arun
 */
public class MsAccessInstanceReader extends JDBCInstanceReader {

	private static final ALogger log = ALoggerFactory.getLogger(MsAccessInstanceReader.class);
	private static final String ENC = "UTF-8";

	private URI uri;

	/**
	 * Default Constructor
	 */
	public MsAccessInstanceReader() {
		super();
	}

	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		MsAccessJdbcIOSupplier inputSource = new MsAccessJdbcIOSupplier(
				new File(source.getLocation()));
		uri = inputSource.getLocation();
		super.setSource(inputSource);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.JDBCInstanceReader#testConnection()
	 */
	@Override
	protected boolean testConnection() throws SQLException {
		// Ms Access database does support Select 1 if it is run in MsAccess
		// Editor but not through UCanAccess Library.
		return true;
	}

	@Override
	public UcanaccessConnection getConnection() throws SQLException {

		String user = getParameter(PARAM_USER).as(String.class);
		String password = getParameter(PARAM_PASSWORD).as(String.class);

		return (UcanaccessConnection) DriverManager.getConnection(getDecodedURI(), user, password);
	}

	private String getDecodedURI() {
		try {
			String decodeURI = URLDecoder.decode(this.uri.toString(), ENC);
			return decodeURI;
		} catch (UnsupportedEncodingException e) {
			log.error(ENC + "! that's supposed to be an encoding!!", e);
			return this.uri.toString();
		}
	}

}
