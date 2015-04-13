package eu.esdihumboldt.hale.io.jdbc.test;

import java.net.URI;


public class DBImageParameters {
	
	/**
	 * The database user name
	 */
	private String user ;
	/**
	 * The database user password
	 */
	private String password ;
	
	/**
	 * The database name
	 */
	private String database;

	private String host ;
	
	private String startUrl;
	
	private int port;
	
	private boolean isPriviliged ;


	/**
	 * The database JDBC URI
	 */
	protected URI databaseUri;


	public DBImageParameters(String user, String password, String database,
			String host, String startUrl, int port, boolean isPriviliged,
			URI databaseUri) {
	
		this.user = user;
		this.password = password;
		this.database = database;
		this.host = host;
		this.startUrl = startUrl;
		this.port = port;
		this.isPriviliged = isPriviliged;
		this.databaseUri = databaseUri;
	}


	public String getUser() {
		return user;
	}


	public String getPassword() {
		return password;
	}


	public String getDatabase() {
		return database;
	}


	public String getHost() {
		return host;
	}


	public String getStartUrl() {
		return startUrl;
	}


	public int getPort() {
		return port;
	}


	public boolean isPriviliged() {
		return isPriviliged;
	}


	public URI getDatabaseUri() {
		return databaseUri;
	}

	public void setDatabaseUri(URI uri){
		this.databaseUri = uri;
	}

	
}
