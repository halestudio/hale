/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.jdbc.extension;

import java.sql.Connection;

/**
 * Interface for extensions configuring a certain connection type.
 * @author Simon Templer
 * @param <T> the concrete connection type
 */
public interface ConnectionConfigurer<T extends Connection> {
	
	/**
	 * Configure a connection after it has been established.
	 * @param connection the JDBC connection
	 */
	public void configureConnection(T connection);

}
