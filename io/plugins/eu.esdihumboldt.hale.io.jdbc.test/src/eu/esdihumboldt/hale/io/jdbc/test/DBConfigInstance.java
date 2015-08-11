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

package eu.esdihumboldt.hale.io.jdbc.test;

import eu.esdihumboldt.hale.common.test.docker.config.DockerConfigInstance;

/**
 * A configuration instance which provides the functionality to get the
 * configuration for starting a database docker image
 * 
 * @author Sameer Sheikh
 */
public class DBConfigInstance extends DockerConfigInstance implements DBImageParameters {

	/**
	 * colon
	 */
	public static final String COLON = ":";
	/**
	 * forward slash
	 */
	public static final String FORW_SLASH = "/";

	/**
	 * @param confKey a configuration key
	 * @param cl class loader
	 */
	public DBConfigInstance(String confKey, ClassLoader cl) {
		super(confKey, cl);

	}

	@Override
	public String getJDBCURL(int port, String hostName) {
		return new StringBuilder().append(getStartURI()).append(hostName).append(COLON)
				.append(port).append(FORW_SLASH).append(getDatabase()).toString();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#getUser()
	 */
	@Override
	public String getUser() {
		return getStringValue(USER_KEY);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#getPassword()
	 */
	@Override
	public String getPassword() {
		return getStringValue(PASSWORD_KEY);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#getDatabase()
	 */
	@Override
	public String getDatabase() {
		return getStringValue(DATABASE_KEY);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#getDBPort()
	 */
	@Override
	public int getDBPort() {
		return getIntValue(PORT_KEY, 0);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#getStartURI()
	 */
	@Override
	public String getStartURI() {
		return getStringValue(START_URL);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#getStartUPTime()
	 */
	@Override
	public int getStartUPTime() {
		return getIntValue(DB_UPTIME, 0);

	}

}
