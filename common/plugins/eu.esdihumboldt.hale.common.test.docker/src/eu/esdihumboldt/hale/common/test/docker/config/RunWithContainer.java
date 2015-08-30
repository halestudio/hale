package eu.esdihumboldt.hale.common.test.docker.config;

/**
 * It exposes functionality to run a unit test logic on a docker client.
 * 
 * @author Sameer Sheikh
 * 
 * @param <T> type
 */
public interface RunWithContainer<T> {

	/**
	 * responsible to run a unit test login on the docker image.
	 * 
	 * @param client a docker client
	 * @param config a docker configuration
	 * @return a type
	 * @throws Exception if fails while executing the run method
	 */
	T run(DockerContainer client, ContainerParameters config) throws Exception;
}
