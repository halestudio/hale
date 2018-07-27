/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc.postgresql;

import java.sql.SQLException;

import org.postgis.PGbox3d;
import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.jdbc.extension.ConnectionConfigurer;

/**
 * Configures a connection to support PostGIS data types.
 * 
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
			connection.addDataType("geometry", PGgeometry.class);
			connection.addDataType("box3d", PGbox3d.class);
		} catch (SQLException e) {
			log.error("Failed to add PostGIS data types support to database connection.", e);
		}
	}

}
