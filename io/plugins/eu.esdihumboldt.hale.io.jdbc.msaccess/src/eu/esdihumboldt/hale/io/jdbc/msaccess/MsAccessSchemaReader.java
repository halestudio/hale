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
import java.net.URI;
import java.sql.DriverManager;
import java.sql.SQLException;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;
import net.ucanaccess.jdbc.UcanaccessConnection;

/**
 * Reads a schema from a MSAccess DB. extended {@link JDBCSchemaReader}.
 * 
 * @author Arun
 */
public class MsAccessSchemaReader extends JDBCSchemaReader {

	private LocatableInputSupplier<? extends InputStream> source;
	private URI uri;

	/**
	 * Default Constructor
	 */
	public MsAccessSchemaReader() {
		setUseQuotes(false);
	}

	@Override
	public LocatableInputSupplier<? extends InputStream> getSource() {
		return source;
	}

	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		this.source = source;
		MsAccessJdbcIOSupplier inputSource = new MsAccessJdbcIOSupplier(
				new File(source.getLocation()));
		uri = inputSource.getLocation();
		super.setSource(inputSource);
	}

	@Override
	protected UcanaccessConnection getConnection() throws SQLException {
		String user = getParameter(PARAM_USER).as(String.class);
		String password = getParameter(PARAM_PASSWORD).as(String.class);

		// return specific UCanAccessConnection
		return (UcanaccessConnection) DriverManager.getConnection(this.uri.toString(), user,
				password);
	}

}
