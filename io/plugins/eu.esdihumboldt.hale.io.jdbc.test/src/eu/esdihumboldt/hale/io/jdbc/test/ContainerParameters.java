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

import java.util.List;

/**
 * TODO Type description
 * 
 * @author sameer
 */
public interface ContainerParameters {

	/**
	 * Exposed ports list key
	 */
	String EXPOSED_PORTS_LIST = ".exposedPorts";
	/**
	 * docker image key
	 */
	String DOCKER_IMAGE = ".dockerImage";
	/**
	 * docker command key
	 */
	String DOCKER_COMMAND = ".command";

	/**
	 * 
	 */
	String EXPOSE_ALL_PORTS = ".exposeAllPorts";
	/**
	 * 
	 */
	String REMOVE = ".remove";

	/**
	 * 
	 */
	String DOCKER_HOST = ".dockerHost";

	/**
	 * 
	 */
	String IS_PRIVILEGED = ".isPrivileged";

	/**
	 * @return
	 */
	String getImageName();

	/**
	 * @return
	 */
	List<String> getexposedPortList();

	/**
	 * @return
	 */
	List<String> getCommands();

	/**
	 * @return
	 */
	boolean isRemove();

	/**
	 * @return
	 */
	boolean isexposeAllPorts();

	/**
	 * @return docker host name
	 */
	String getDockerHost();

	/**
	 * @return if the container needs to start in privileged mode
	 */
	boolean isPrivileged();

}
