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

package eu.esdihumboldt.hale.common.test.docker.config;

import java.util.List;

import com.typesafe.config.Config;

/**
 * Parameters related to docker
 * 
 * @author Sameer Sheikh
 */
public interface ContainerParameters {

	/**
	 * a configuration key for exposed ports list attribute
	 */
	String EXPOSED_PORTS_LIST = "exposedPorts";

	/**
	 * a configuration key for docker image attribute
	 */
	String DOCKER_IMAGE = "dockerImage";

	/**
	 * a configuration key for command attribute
	 */
	String DOCKER_COMMAND = "command";

	/**
	 * a configuration key for exposeAllPorts attribute
	 */
	String EXPOSE_ALL_PORTS = "exposeAllPorts";

	/**
	 * A configuration key for docker host attribute
	 */
	String DOCKER_HOST = "dockerHost";

	/**
	 * A configuration key which has a boolean value whether to start a
	 * container with privileged mode or not.
	 */
	String IS_PRIVILEGED = "isPrivileged";

	/**
	 * @return the image name configured in the configuration file
	 */
	String getImageName();

	/**
	 * @return exposed port list configured in the configuration file
	 */
	List<String> getExposedPortList();

	/**
	 * @return commands configured in the configuration file
	 */
	List<String> getCommands();

	/**
	 * @return exposed All ports flag value configured in the configuration file
	 */
	boolean isexposeAllPorts();

	/**
	 * @return docker host name configured in the configuration file
	 */
	String getDockerHost();

	/**
	 * This tells whether to start a container in privileged mode or not.
	 * 
	 * @return is_Privileged flag value configured in the configuration file
	 */
	boolean isPrivileged();

	/**
	 * gets a string value from a config map which maps the config key with the
	 * config value.
	 * 
	 * @param key a configuration key
	 * @return string value associated with the given key
	 */
	public String getStringValue(String key);

	/**
	 * gets a list values from a config map which maps the config key with the
	 * config value.
	 * 
	 * @param key a config key
	 * @return list value associated with the given key
	 */
	public List<String> getListValues(String key);

	/**
	 * gets a boolean value from a config map which maps the config key with the
	 * config value.
	 * 
	 * @param key a config key
	 * @return boolean value associated with the given key
	 */
	public boolean getBooleanValue(String key);

	/**
	 * gets a int value from a config map which maps the config key with the
	 * config value.
	 * 
	 * @param key a config key
	 * @return int value associated with the given key
	 */
	public int getIntValue(String key);

	/**
	 * returns a config which maps configuration key path to a configuration
	 * value.
	 * 
	 * @return a config
	 */
	public Config getConfig();
}
