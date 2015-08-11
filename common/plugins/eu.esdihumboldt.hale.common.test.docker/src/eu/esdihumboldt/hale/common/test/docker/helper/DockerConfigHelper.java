package eu.esdihumboldt.hale.common.test.docker.helper;

import eu.esdihumboldt.hale.common.test.docker.config.DockerConfigInstance;
import eu.esdihumboldt.hale.common.test.docker.config.HaleDockerClient;
import eu.esdihumboldt.hale.common.test.docker.config.RunWithContainer;

/**
 * The docker config helper which cretaes a docker container, starts a
 * container, executes the run method from a anonymous class, and then kills the
 * container.
 * 
 * @author Sameer Sheikh
 * 
 */
public class DockerConfigHelper {

	/**
	 * fetches the configuration parameters for the key <b> configName </b>
	 * which is usefull in creating a docker container. It starts a container,
	 * executes the logic and kills the container.
	 * 
	 * @param configName a name for the configuration group
	 * @param runInstance an anonymous class object
	 * @return the result of run method execution of a RunWithContainer class
	 * @throws Exception exception if it fails to kill the container
	 */
	public static <T> T withContainer(String configName, RunWithContainer<T> runInstance)
			throws Exception {

		DockerConfigInstance dci = new DockerConfigInstance(configName);
		HaleDockerClient client = new HaleDockerClient(dci);
		client.createContainer();
		try {
			client.startContainer();
			return runInstance.run(client, dci);

		} finally {
			client.killAndRemoveContainer();
		}
	}
}
