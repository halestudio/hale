/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc;

/**
 * Constants related to JDBC I/O providers.
 * 
 * @author Simon Templer
 */
public interface JDBCConstants {

	/**
	 * The identifier of the JDBC content type.
	 */
	public static final String CONTENT_TYPE_ID = "eu.esdihumboldt.hale.io.jdbc";

	/**
	 * Parameter name for the connection user name
	 */
	public static final String PARAM_USER = "jdbc.user";

	/**
	 * Parameter name for the connection user password
	 */
	public static final String PARAM_PASSWORD = "jdbc.password";

	/**
	 * Parameter name for the selected schemas. The value is a comma-separated
	 * list of schema names.
	 */
	public static final String SCHEMAS = "schemas";

}
