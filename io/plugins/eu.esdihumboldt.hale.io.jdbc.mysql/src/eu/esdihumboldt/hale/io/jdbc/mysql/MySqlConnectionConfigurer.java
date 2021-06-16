package eu.esdihumboldt.hale.io.jdbc.mysql;

import com.mysql.cj.jdbc.JdbcConnection;

import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

public class MySqlConnectionConfigurer implements ConnectionConfigurer<JdbcConnection> {

	@Override
	public void configureConnection(JdbcConnection connection) {
		// TODO Auto-generated method stub
		//Einträge in Extension ergänzen - Vorbild in PostgreSQL
		//checken ob verfügbar (und breakpoint greift)
	}
	
}
