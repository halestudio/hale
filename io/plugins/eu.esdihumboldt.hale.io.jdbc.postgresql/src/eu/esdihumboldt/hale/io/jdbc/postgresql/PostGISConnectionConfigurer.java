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

package eu.esdihumboldt.hale.io.jdbc.postgresql;

import java.sql.SQLException;

import org.postgresql.PGConnection;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

/**
 * Configures a connection to support PostGIS data types.
 * @author Simon Templer
 */
public class PostGISConnectionConfigurer implements ConnectionConfigurer<PGConnection> {
	
	private static final ALogger log = ALoggerFactory.getLogger(PostGISConnectionConfigurer.class);

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer#configureConnection(java.lang.Object)
	 */
	@Override
	public void configureConnection(PGConnection connection) {
		try {
			connection.addDataType("geometry",Class.forName("org.postgis.PGgeometry"));
			connection.addDataType("box3d",Class.forName("org.postgis.PGbox3d"));
		} catch (SQLException e) {
			log.error("Failed to add PostGIS data types support to database connection.", e);
		} catch (ClassNotFoundException e) {
			log.error("Failed to load PostGIS data type classes.", e);
		}
	}

}
