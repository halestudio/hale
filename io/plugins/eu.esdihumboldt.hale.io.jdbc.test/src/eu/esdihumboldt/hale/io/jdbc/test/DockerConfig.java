package eu.esdihumboldt.hale.io.jdbc.test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DockerConfig {
	public static final String DOCKER_CONF = "docker.conf";
	public static final String DOCKER_CONF_DIR = ".overcast";
	public static final String DOCKER_HOST = ".dockerHost";
	public static final String DOCKER_IMAGE = ".dockerImage";
	public static final String DOCKER_COMMAND = ".command";
	public static final String DOCKER_REMOVE = ".remove";
	public static final String EXPOSED_ALL_PORTS = ".exposeAllPorts";
	public static final String EXPOSED_PORTS_LIST = ".exposedPorts";
	private static final String DOCKER_CONF_FILE = "docker.conf.file";

	/**
	 * Gets the file from user directory/.overcast/docker.conf
	 * 
	 * @return the absolute path of the file
	 */
	private static String getHomeDirConf() {

		return new File(new File(System.getProperty("user.home"),
				DOCKER_CONF_DIR), DOCKER_CONF).getAbsolutePath();

	}

	private static String getWorkDirConf() {

		return new File(System.getProperty("user.dir"), DOCKER_CONF)
				.getAbsolutePath();
	}

	/**
	 * get the Docker config
	 * 
	 * @param thisClass
	 * @return config
	 */

	public static Config getDockerConfig() {
		return ConfigFactory
				.systemProperties()
				.withFallback(
						loadConfigFromPath(System.getProperty(DOCKER_CONF_FILE)))
				.withFallback(loadConfigFromPath(getHomeDirConf()))
				.withFallback(loadConfigFromPath(getWorkDirConf()))
				.withFallback(loadConfigFromClassPath()).resolve();
	}

	/**
	 * loads the config file from the class path
	 * 
	 * @param thisClass
	 * @return config
	 */

	private static Config loadConfigFromClassPath() {

		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(DOCKER_CONF);

		if (is == null)
			return ConfigFactory.empty();
		else
			return ConfigFactory.parseReader(new InputStreamReader(is));
	}

	/**
	 * loads the config from the given path
	 * 
	 * @param path
	 * @return Config
	 */
	private static Config loadConfigFromPath(String path) {
		if (path == null) {
			return ConfigFactory.empty();
		}
		File f = new File(path);
		if (!f.exists()) {
			return ConfigFactory.empty();
		}

		return ConfigFactory.parseFile(f);

	}

}
