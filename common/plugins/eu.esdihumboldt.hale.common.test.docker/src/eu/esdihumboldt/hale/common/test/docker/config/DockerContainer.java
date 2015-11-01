package eu.esdihumboldt.hale.common.test.docker.config;

/**
 * A generic docker client
 * 
 * @author Sameer Sheikh
 * 
 */
public interface DockerContainer {

	/**
	 * gets the host name for a docker client
	 * 
	 * @return host name
	 */
	String getHostName();

	/**
	 * gets the host port of a docker client for a local port number
	 * 
	 * @param port local port number
	 * @return host port number
	 */
	int getHostPort(int port);
}
