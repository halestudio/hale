package eu.esdihumboldt.hale.common.test.docker.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;

/**
 * A Hale docker client
 * 
 * @author Sameer Sheikh
 * 
 */
public class HaleDockerClient implements DockerContainer {

	private DockerClient dc;
	private ContainerConfig containerConf;
	private Map<String, List<PortBinding>> portMapper;
	private String containerId;
	private URI uri;
	private final ContainerParameters dbc;
	private ContainerCreation creation;

	/**
	 * A parameterized constructor
	 * 
	 * @param dbc parameters related to a general docker client
	 */
	public HaleDockerClient(ContainerParameters dbc) {
		this.dbc = dbc;

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
	 * start a container. Container can be started in the privileged mode if the
	 * 'isPrivileged' key in the configuration is set as true.
	 * 
	 * @throws DockerException docker exception
	 * @throws InterruptedException interrupted exception
	 */
	public void startContainer() throws DockerException, InterruptedException {

		creation = dc.createContainer(containerConf);
		containerId = creation.id();

		final HostConfig hostConfig = HostConfig.builder().publishAllPorts(dbc.isExposeAllPorts())
				.privileged(dbc.isPrivileged()).build();

		dc.startContainer(containerId, hostConfig);

		final ContainerInfo info = dc.inspectContainer(containerId);
		portMapper = info.networkSettings().ports();

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

		try {
			dc.killContainer(containerId);
		} finally {
			dc.removeContainer(containerId);
		}

	}

	public String getContainerId() {
		return creation.id();
	}
}
