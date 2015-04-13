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
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author Sameer Sheikh
 * 
 */
public class DBDockerClient {
	private DockerClient dc;
	private ContainerConfig config;
	private Map<String, List<PortBinding>> portMapper;
	private String containerId;
	private Config conf;
	private URI uri;
	/**
	 * 
	 * @param path
	 */
	public DBDockerClient(String path) {
		conf = DockerConfig.getDockerConfig();
		createContainer(conf, path);

	}

	/**
	 * create a container
	 * 
	 * @param conf
	 *            config
	 * 
	 * @param path
	 *            host Path
	 */
	public void createContainer(Config conf, String path) {

		if (conf.hasPath(path)) {
			
			Set<String> exposedPorts = new HashSet<String>(getConfListValue(path
					+ DockerConfig.EXPOSED_PORTS_LIST));
			 uri = URI.create(conf.getString(path
					+ DockerConfig.DOCKER_HOST));
			dc = new DefaultDockerClient(uri);
			
			config = ContainerConfig
					.builder()
					.image(getConfValue(path + DockerConfig.DOCKER_IMAGE))
					.cmd(getConfListValue(path + DockerConfig.DOCKER_COMMAND))
					.exposedPorts(exposedPorts).build();

		}

	}
	
	public String getConfValue(String key){
	
		if(conf.hasPath(key)){
			return conf.getString(key);
		}
		else
			return null;	
		
	}
	
	public Config getConfig(){
		if(conf== null)
			return ConfigFactory.empty();
		return conf;
	}
	public List<String> getConfListValue(String key){
		
		if(conf.hasPath(key)){
			return conf.getStringList(key);
			
		}
		else
			return new ArrayList<String>();
	}
	
	public boolean getConfBooleanValue(String key){
		
		if (conf.hasPath(key)){
			return conf.getBoolean(key);
		}
		else
			return false;
	}
	
	public String getHostName(){
		return uri.getHost();
	}

	/**
	 * start a container in privilged mode
	 */
	public void startContainer(boolean isPriviliged) {
		
		ContainerCreation creation;
		try {
			
			creation = dc.createContainer(config);
			containerId = creation.id();

			final HostConfig hostConfig = HostConfig.builder()
					.publishAllPorts(true).privileged(isPriviliged).build();

			dc.startContainer(containerId, hostConfig);
			
			final ContainerInfo info = dc.inspectContainer(containerId);
			portMapper = info.networkSettings().ports();
			

		} catch (DockerException | InterruptedException e) {
			
			e.printStackTrace();
			
		}

	}

	/**
	 * get the host port
	 * 
	 * @param port
	 *            host port
	 * @return
	 */
	public int getPort(int port) {

		ArrayList<PortBinding> bindings = (ArrayList<PortBinding>) portMapper
				.get(port + "/tcp");

		if (bindings != null && bindings.size() > 0) {

			return Integer.parseInt(bindings.get(0).hostPort());

		} else {

			throw new IllegalArgumentException("Port not available");

		}

	}

	/**
	 * kill and remove the container
	 */
	public void killAndRemoveContainer() {
		try {
			dc.killContainer(containerId);
			dc.removeContainer(containerId);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
