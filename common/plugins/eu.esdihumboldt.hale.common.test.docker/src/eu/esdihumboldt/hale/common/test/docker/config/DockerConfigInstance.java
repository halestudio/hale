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
	 */
	public DockerConfigInstance(String confKey) {
		this.configKey = confKey;
		conf = DockerConfig.getDockerConfig();
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

	@Override
	public String getDockerHost() {
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

}
