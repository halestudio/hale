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

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;

/**
 * TODO Type description
 * 
 * @author sameer sheikh
 */
public class DBConfigInstance implements DBImageParameters, ContainerParameters {

	/**
	 * colon
	 */
	public static final String COLON = ":";
	/**
	 * forward slash
	 */
	public static final String FORW_SLASH = "/";
	private final Config conf;
	private final String confKey;

	/**
	 * @param confKey
	 */
	public DBConfigInstance(String confKey) {
		this.confKey = confKey;
		this.conf = DockerConfig.getDockerConfig();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.ConfigParameters#getJDBCURI(java.lang.String,
	 *      java.lang.String, int)
	 */
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
		if (conf.hasPath(confKey + PORT_KEY)) {
			return conf.getInt(confKey + PORT_KEY);
		}
		return 0;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#isPrivileged()
	 */
	@Override
	public boolean isPrivileged() {
		return getBooleanValue(IS_PRIVILEGED);
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

		if (conf.hasPath(confKey + DB_UPTIME)) {
			return conf.getInt(confKey + DB_UPTIME);
		}
		return 0;

	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.ContainerParameters#getImageName()
	 */
	@Override
	public String getImageName() {
		return getStringValue(DOCKER_IMAGE);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.ContainerParameters#getexposedPortList()
	 */
	@Override
	public List<String> getexposedPortList() {
		return getListValues(EXPOSED_PORTS_LIST);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.ContainerParameters#getCommands()
	 */
	@Override
	public List<String> getCommands() {
		return getListValues(DOCKER_COMMAND);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.ContainerParameters#isRemove()
	 */
	@Override
	public boolean isRemove() {
		return getBooleanValue(REMOVE);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.ContainerParameters#isexposeAllPorts()
	 */
	@Override
	public boolean isexposeAllPorts() {
		return getBooleanValue(EXPOSE_ALL_PORTS);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.test.DBImageParameters#dockerHost()
	 */
	@Override
	public String getDockerHost() {
		return getStringValue(DOCKER_HOST);

	}

	private String getStringValue(String key) {

		if (conf.hasPath(confKey + key)) {
			return conf.getString(confKey + key);
		}
		return null;

	}

	/**
	 * @param exposedPortsList
	 * @return
	 */
	private List<String> getListValues(String key) {

		if (conf.hasPath(confKey + key)) {
			return conf.getStringList(confKey + key);
		}
		return new ArrayList<String>();

	}

	private boolean getBooleanValue(String key) {

		if (conf.hasPath(confKey + key)) {
			return conf.getBoolean(confKey + key);
		}
		return false;

	}

}
