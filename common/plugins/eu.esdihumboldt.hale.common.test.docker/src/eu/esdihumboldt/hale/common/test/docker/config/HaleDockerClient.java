package eu.esdihumboldt.hale.common.test.docker.config;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.ImageNotFoundException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * A Hale docker client
 * 
 * @author Sameer Sheikh
 * 
 */
public class HaleDockerClient implements DockerContainer {

	private static final ALogger LOGGER = ALoggerFactory.getLogger(HaleDockerClient.class);

	private DockerClient dc;
	private ContainerConfig containerConf;
	private Map<String, List<PortBinding>> portMapper;
	private String containerId;
	private URI uri;
	private final ContainerParameters dbc;
	private ContainerCreation creation;

	private String containerIp;

	/**
	 * A parameterized constructor
	 * 
	 * @param dbc parameters related to a general docker client
	 */
	public HaleDockerClient(ContainerParameters dbc) {
		this.dbc = dbc;

	}

	/**
	 * Checks the availability of the Docker server.
	 * 
	 * @return <code>true</code> if the Docker server is available,
	 *         <code>false</code> otherwise
	 */
	public boolean isServerAvailable() {
		if (dc == null) {
			throw new IllegalStateException(
					"Docker client not created yet: call createContainer() first");
		}

		int numAttempts = 3;
		for (int i = 0; i < numAttempts; i++) {
			try {
				String result = dc.ping();
				if ("OK".equals(result)) {
					return true;
				}
			} catch (Exception e) {
				int attemptsLeft = numAttempts - (i + 1);
				LOGGER.debug("Exception occurred connection to docker server: " + attemptsLeft
						+ " attempts left", e);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// ignore
				}
			}
		}

		return false;
	}

	/**
	 * creates a container using the parameters.
	 * 
	 */
	public void createContainer() {

		Set<String> exposedPorts = new HashSet<String>(dbc.getExposedPortList());
		uri = URI.create(dbc.getDockerHost());
		dc = new DefaultDockerClient(uri);

		containerConf = ContainerConfig.builder().image(dbc.getImageName()).cmd(dbc.getCommands())
				.exposedPorts(exposedPorts).build();

	}

	/**
	 * Gets the host name from the url configured in the docker configuration
	 * 
	 * @return host name configured
	 */
	@Override
	public String getHostName() {
		return uri.getHost();
	}

	/**
	 * @return the container IP address
	 */
	public String getContainerIp() {
		return containerIp;
	}

	/**
	 * start a container. Container can be started in the privileged mode if the
	 * 'isPrivileged' key in the configuration is set as true.
	 * 
	 * @throws DockerException docker exception
	 * @throws InterruptedException interrupted exception
	 */
	public void startContainer() throws DockerException, InterruptedException {
		try {
			dc.inspectImage(containerConf.image());
		} catch (ImageNotFoundException e) {
			// pull image if it is not present
			LOGGER.info(
					MessageFormat.format("Docker image not found, attempting to pull image {0}...",
							containerConf.image()));
			dc.pull(containerConf.image());
		}
		// TODO also add a setting to pull the image always?

		LOGGER.info(MessageFormat.format("Preparing container for image {0}...",
				containerConf.image()));
		creation = dc.createContainer(containerConf);
		containerId = creation.id();
		LOGGER.info(MessageFormat.format("Created container with ID {0}, now starting...",
				containerId));

		final HostConfig hostConfig;
		if (getHostName() == null) {
			// don't publish ports (probably unix socket connection)
			hostConfig = HostConfig.builder().publishAllPorts(false).privileged(dbc.isPrivileged())
					.build();
		}
		else {
			// XXX publishing all ports can be very bad if the host is
			// accessible externally
			hostConfig = HostConfig.builder().publishAllPorts(dbc.isExposeAllPorts())
					.privileged(dbc.isPrivileged()).build();
		}

		dc.startContainer(containerId, hostConfig);

		final ContainerInfo info = dc.inspectContainer(containerId);
		portMapper = info.networkSettings().ports();
		containerIp = info.networkSettings().ipAddress();
	}

	/**
	 * gets the binded docker host port
	 * 
	 * @param port the configured port number
	 * @return the binded docker host port number for the given port
	 */
	@Override
	public int getHostPort(int port) {

		ArrayList<PortBinding> bindings = (ArrayList<PortBinding>) portMapper.get(port + "/tcp");

		if (bindings != null && bindings.size() > 0) {

			return Integer.parseInt(bindings.get(0).hostPort());

		}
		else {

			throw new IllegalArgumentException("Port not available");

		}

	}

	/**
	 * kill and remove the container
	 * 
	 * @throws Exception if fails to kill the container or remove it
	 */
	public void killAndRemoveContainer() throws Exception {
		if (containerId == null) {
			return;
		}
		try {
			LOGGER.info(MessageFormat.format("Killing container {0}...", containerId));
			dc.killContainer(containerId);
		} finally {
			LOGGER.info(MessageFormat.format("Removing container {0}...", containerId));
			dc.removeContainer(containerId);
		}
	}

	/**
	 * @return the container ID or <code>null</code> if no container was created
	 *         yet
	 */
	public String getContainerId() {
		if (creation != null) {
			return creation.id();
		}
		else {
			return null;
		}
	}
}
