package eu.esdihumboldt.hale.io.jdbc.test;

/**
 * Parameters related to database docker configuration
 * 
 * @author sameer sheikh
 */
public interface DBImageParameters extends ContainerParameters {

	String USER_KEY = ".user";
	String PASSWORD_KEY = ".password";
	String DATABASE_KEY = ".database";
	String PORT_KEY = ".port";

	String START_URL = ".startURL";
	String DB_UPTIME = ".dbUPTime";

	/**
	 * @param tdc
	 * @return
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

	int getStartUPTime();

}
