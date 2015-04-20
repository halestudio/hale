package eu.esdihumboldt.hale.io.jdbc.test;

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
 * @author Sameer Sheikh
 * 
 */
public class DBDockerClient {

	private DockerClient dc;
	private ContainerConfig containerConf;
	private Map<String, List<PortBinding>> portMapper;
	private String containerId;
	private URI uri;
	private final ContainerParameters dbc;

	/**
	 * 
	 * @param conf
	 * @param path
	 */
	public DBDockerClient(ContainerParameters dbc) {
		this.dbc = dbc;

	}

	/**
	 * create a container
	 * 
	 * @param conf config
	 * 
	 * @param path host Path
	 */
	public void createContainer() {

		Set<String> exposedPorts = new HashSet<String>(dbc.getexposedPortList());
		uri = URI.create(dbc.getDockerHost());
		dc = new DefaultDockerClient(uri);

		containerConf = ContainerConfig.builder().image(dbc.getImageName()).cmd(dbc.getCommands())
				.exposedPorts(exposedPorts).build();

	}

	/**
	 * @param key key
	 * @return boolean value
	 * 
	 *         /**
	 * @return get docker host name
	 */
	public String getHostName() {
		return uri.getHost();
	}

	/**
	 * start a container in privilged mode
	 * 
	 * @param isPriviliged to start container in privileged mode or not
	 * @throws DockerException docker exception
	 * @throws InterruptedException interrupted exception
	 */
	public void startContainer() throws DockerException, InterruptedException {

		ContainerCreation creation;
		creation = dc.createContainer(containerConf);
		containerId = creation.id();

		final HostConfig hostConfig = HostConfig.builder().publishAllPorts(true)
				.privileged(dbc.isPrivileged()).build();

		dc.startContainer(containerId, hostConfig);

		final ContainerInfo info = dc.inspectContainer(containerId);
		portMapper = info.networkSettings().ports();

	}

	/**
	 * get the host port
	 * 
	 * @param port host port
	 * @return
	 */
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
	 * @throws Exception
	 */
	public void killAndRemoveContainer() throws Exception {

		dc.killContainer(containerId);
		dc.removeContainer(containerId);

	}

}
