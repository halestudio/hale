package eu.esdihumboldt.hale.common.test.docker.config;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;

/**
 * A general docker config instance
 * 
 * @author Sameer Sheikh
 * 
 */
public class DockerConfigInstance implements ContainerParameters {

	private static final String DOT = ".";
	private final String configKey;
	private final Config conf;

	/**
	 * Parameterized constructor
	 * 
	 * @param confKey a key for the configuration group
	 * @param cl a class loader to fetch the configuration from class path
	 */
	public DockerConfigInstance(String confKey, ClassLoader cl) {

		this.configKey = confKey;
		conf = DockerConfig.getDockerConfig(cl);

	}

	@Override
	public String getImageName() {
		return getStringValue(DOCKER_IMAGE);
	}

	@Override
	public List<String> getExposedPortList() {
		return getListValues(EXPOSED_PORTS_LIST);
	}

	@Override
	public List<String> getCommands() {
		return getListValues(DOCKER_COMMAND);
	}

	@Override
	public boolean isexposeAllPorts() {
		return getBooleanValue(EXPOSE_ALL_PORTS);
	}

	/**
	 * It checks if the global configuration for the docker host is available.
	 * It overrides the local configuration for the docker host and takes the
	 * global configuration.
	 * 
	 * @see eu.esdihumboldt.hale.common.test.docker.config.ContainerParameters#getDockerHost()
	 */
	@Override
	public String getDockerHost() {

		Config global = null;
		try {
			global = getConfig(GLOBAL);
		} catch (Exception e) {
			// do nothing
		}

		if (global != null && global.hasPath(DOCKER_HOST)) {
			return global.getString(DOCKER_HOST);
		}
		return getStringValue(DOCKER_HOST);
	}

	@Override
	public boolean isPrivileged() {
		return getBooleanValue(IS_PRIVILEGED);
	}

	@Override
	public String getStringValue(String key) {
		String valueKey = configKey + DOT + key;

		if (conf.hasPath(valueKey)) {
			return conf.getString(valueKey);
		}
		return null;

	}

	@Override
	public List<String> getListValues(String key) {
		String valueKey = configKey + DOT + key;

		if (conf.hasPath(valueKey)) {
			return conf.getStringList(valueKey);
		}
		return new ArrayList<String>();

	}

	@Override
	public boolean getBooleanValue(String key) {
		String valueKey = configKey + DOT + key;

		if (conf.hasPath(valueKey)) {
			return conf.getBoolean(valueKey);
		}
		return false;

	}

	@Override
	public int getIntValue(String key) {
		String valueKey = configKey + DOT + key;

		if (conf.hasPath(valueKey)) {
			return conf.getInt(valueKey);
		}
		return 0;
	}

	@Override
	public Config getConfig() {
		return conf.getConfig(configKey);
	}

	/**
	 * @param keyValue a key path to the config
	 * @return a config object associated at the given key path
	 * @throws Exception if it fails to find the config at given path
	 */
	public Config getConfig(String keyValue) throws Exception {
		return conf.getConfig(keyValue);
	}

}
