/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.io.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import eu.esdihumboldt.hale.common.core.io.IOProvider;

/**
 * Common interface for JDBC I/O providers.
 * 
 * @author Simon Templer
 */
public interface JDBCProvider extends IOProvider {

	/**
	 * Get a database connection.
	 * 
	 * @return Connection object after loading driver
	 * @throws SQLException if the connection could not be established
	 */
	Connection getConnection() throws SQLException;

}
