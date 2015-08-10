package eu.esdihumboldt.hale.io.jdbc.test;

import eu.esdihumboldt.hale.common.test.docker.config.ContainerParameters;

/**
 * Parameters related to database docker configuration
 * 
 * @author sameer sheikh
 */
public interface DBImageParameters extends ContainerParameters {

	/**
	 * Configuration key for a username
	 */
	String USER_KEY = "user";
	/**
	 * Configuration key for password
	 */
	String PASSWORD_KEY = "password";

	/**
	 * Configuration key for a database name
	 */
	String DATABASE_KEY = "database";
	/**
	 * Configuration key for a port number
	 */
	String PORT_KEY = "port";

	/**
	 * Configuration key for a start url for a database connection
	 */
	String START_URL = "startURL";
	/**
	 * Configuration key for a DB uptime which is the time taken by docker
	 * database image to get started
	 */
	String DB_UPTIME = "dbUPTime";

	/**
	 * It creates a JDBC url for a database connection.
	 * 
	 * @param port port number
	 * @param hostName a hopstname
	 * @return a jdbc url for a database connection
	 */
	String getJDBCURL(int port, String hostName);

	/**
	 * @return get username
	 */
	String getUser();

	/**
	 * @return get password
	 */
	String getPassword();

	/**
	 * @return get database name
	 */
	String getDatabase();

	/**
	 * @return port number
	 */
	int getDBPort();

	/**
	 * @return it is the start of the jdbc uri.
	 */
	String getStartURI();

	/**
	 * @return get start up time required to start a database docker image
	 */
	int getStartUPTime();

}
