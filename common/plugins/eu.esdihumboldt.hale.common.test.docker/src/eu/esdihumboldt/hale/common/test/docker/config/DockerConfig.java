package eu.esdihumboldt.hale.common.test.docker.config;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * A docker config class which is responsible for getting the docker
 * configuration information. It searches for the configuration in System
 * properties, working directory, home directory and classpath.
 * <p>
 * <strong>Example </strong></br> Mandatory configurations are:
 * <ul>
 * <li>dockerHost</li>
 * <li>dockerImage</>
 * </ul>
 * </br>Optional configuration are:
 * <ul>
 * <li>exposedPorts (default: exposed ports in the image, if mentioned then it
 * will be added to the image's exposed port list )</li>
 * <li>exposeAllPorts (default: true, while starting a container, if all ports
 * to be exposed for providing services outside the container.) This should be
 * true to start the communication with the container from HALE.</>
 * <li>command (default: commands used in the image will be used)</>
 * <li>isPrivileged (default: false)</>
 * </ul>
 * 
 * The docker configuration example is as below:
 * 
 * <pre>
 * dockertest{
 *     dockerHost="http://192.168.59.103:2375"
 *     dockerImage="stempler/postgis" // "kartoza/postgis"
 *     exposeAllPorts=true
 *     exposedPorts=["5432/tcp"]
 *     command=["/start-postgis.sh"]
 *     isPrivileged=false
 *    
 * }
 * </pre>
 * 
 * <p>
 * <h2>Preference order</h2>
 * Hale looks for the docker configuration in the following files in the
 * following order:
 * <ul>
 * <li>System.getProperty("docker.conf.file")</li>
 * <li>HOMEDIR/.hale/hale-docker.conf</li>
 * <li>WORKDIR/hale-docker.conf</li>
 * <li>CLASSPATH/hale-docker.conf</li>
 * </ul>
 * 
 * Configuration in home directory takes precedence over configuration in
 * working directory. That means, user can override the configuration with the
 * updated one without changing project setup.
 * <p>
 * <strong>Global configuration </strong> Hale gives more precedence to the
 * global docker host configuration than the embedded one. <strong> Example
 * </strong> </br></br> global{</br> dockerHost="http://192.168.59.103:2375"
 * </br>}</br></br> takes more precedence over </br></br> postgis{</br>
 * dockerHost="http://127.0.0.1:2375"</br> }
 * 
 * 
 * 
 * @author Sameer Sheikh
 * 
 */
public class DockerConfig {

	/**
	 * Docker configuration file key
	 */
	public static final String DOCKER_CONF = "hale-docker.conf";
	/**
	 * docker configuration directory key
	 */
	public static final String DOCKER_CONF_DIR = ".hale";

	/**
	 * docker conf file (for configuration set on system property.
	 */
	public static final String DOCKER_CONF_FILE = "docker.conf.file";

	/**
	 * Looks for the configuration file in the home directory
	 * 
	 * @return the absolute path of the configuration file in the home directory
	 */
	private static String getHomeDirConf() {

		return new File(new File(System.getProperty("user.home"), DOCKER_CONF_DIR), DOCKER_CONF)
				.getAbsolutePath();

	}

	/**
	 * Looks for the configuration file in the working directory
	 * 
	 * @return the absolute path of the configuration file in the working
	 *         directory
	 */
	private static String getWorkDirConf() {

		return new File(System.getProperty("user.dir"), DOCKER_CONF).getAbsolutePath();
	}

	/**
	 * Checks for the configuration file in the system properties, home
	 * directory, working directory and then class path.
	 * 
	 * @param cl a class loader to fetch the configuration from class path
	 * 
	 * @return A config object which maps configuration keys to configuration
	 *         values.
	 */

	public static Config getDockerConfig(ClassLoader cl) {
		return ConfigFactory.systemProperties()
				.withFallback(loadConfigFromPath(System.getProperty(DOCKER_CONF_FILE)))
				.withFallback(loadConfigFromPath(getHomeDirConf()))
				.withFallback(loadConfigFromPath(getWorkDirConf()))
				.withFallback(loadConfigFromClassPath(cl)).resolve();
	}

	/**
	 * gets the configuration file from the class path.
	 * 
	 * @param cl a class loader to fetch the configuration from class path
	 * 
	 * @return config object which maps config key to config value
	 */

	private static Config loadConfigFromClassPath(ClassLoader cl) {

		InputStream is = cl.getResourceAsStream(DOCKER_CONF);

		if (is == null)
			return ConfigFactory.empty();
		else
			return ConfigFactory.parseReader(new InputStreamReader(is));
	}

	/**
	 * loads the config from the given path
	 * 
	 * @param path config path
	 * @return config which maps config key to config value
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
