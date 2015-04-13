/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.postgresql.test;

/**
 * Postgresql db setup constants
 * 
 * @author Sameer Sheikh
 */
public interface PostSetup {

	/**
	 * The database user
	 */
	String USER = "docker";

	/**
	 * The database user password
	 */
	String PASSWORD = "docker";

	/**
	 * The database name
	 */
	String DATABASE = "gis";

	/**
	 * The Docker.conf configuration host key
	 */
	String HOST = "postgis";

	/**
	 * Start of host URI
	 */
	String START_URL = "jdbc:postgresql://";

	/**
	 * Host port number
	 */
	int PORT = 5432;

	/**
	 * is privileged?
	 */
	boolean IS_PRIVILEGED = false;

}
