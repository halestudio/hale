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

import java.sql.ResultSet;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

/**
 * A connection configurer for MS SQL Server database
 * 
 * @author Arun
 */
public class MsSqlConnectionConfigurer implements ConnectionConfigurer<SQLServerConnection> {

	private static final ALogger log = ALoggerFactory.getLogger(MsSqlConnectionConfigurer.class);

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer#configureConnection(java.lang.Object)
	 */
	@Override
	public void configureConnection(SQLServerConnection connection) {
		try {
			// SQL Server supports holdability at the connection level only.
			connection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT);
		} catch (Exception e) {
			log.warn("Fail to set holdability on SQL Server connection", e);
		}

	}

}
