/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.jdbc.spatialite.internal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.sqlite.SQLiteConnection;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Factory to create {@link SpatiaLiteSupport} instances matching the version of
 * the connected database.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteSupportFactory {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractSpatiaLiteSupport.class);

	private static SpatiaLiteSupportFactory instance;

	private final ConcurrentHashMap<String, SpatiaLiteSupport> cache;

	private SpatiaLiteSupportFactory() {
		cache = new ConcurrentHashMap<String, SpatiaLiteSupport>();
	}

	/**
	 * Return the singleton factory instance.
	 * 
	 * @return the factory instance
	 */
	public static SpatiaLiteSupportFactory getInstance() {
		if (instance == null) {
			instance = new SpatiaLiteSupportFactory();
		}

		return instance;
	}

	/**
	 * Create a {@link SpatiaLiteSupport} instances matching the version of the
	 * connected database.
	 * <p>
	 * {@link SpatiaLiteSupport} instances are internally cached; the connection
	 * URL is used as cache key, so if the method is invoked multiple times
	 * providing connection objects pointing to the same physical DB, only one
	 * instance of {@link SpatiaLiteSupport} is created and re-used multiple
	 * times.
	 * </p>
	 * 
	 * @param connection the DB connection
	 * @return the proper {@link SpatiaLiteSupport} instance
	 */
	public SpatiaLiteSupport createSpatiaLiteSupport(SQLiteConnection connection) {

		String key = connection.getUrl();
		SpatiaLiteSupport support = cache.get(key);
		if (support != null) {
			return support;
		}

		int version = determineSpatiaLiteVersion(connection);
		if (version == 3) {
			support = new SpatiaLiteSupportVersion3();
		}
		else if (version == 4) {
			support = new SpatiaLiteSupportVersion4();
		}
		else {
			throw new IllegalStateException("Unable to determine SpatiaLite version");
		}

		cache.putIfAbsent(key, support);

		return cache.get(key);
	}

	/**
	 * Determine SpatiaLite version parsing the structure of the
	 * <code>geometry_columns</code> table.
	 * 
	 * @param connection the DB connection
	 * @return the version number
	 */
	private int determineSpatiaLiteVersion(SQLiteConnection connection) {
		final String V3_GEOM_COL_NAME = "type";
		final String V3_GEOM_COL_TYPE = "TEXT";
		final String V4_GEOM_COL_NAME = "geometry_type";
		final String V4_GEOM_COL_TYPE = "INTEGER";

		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement("PRAGMA table_info(\"geometry_columns\")");

			rs = stmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				String type = rs.getString("type");

				if (name.equals(V3_GEOM_COL_NAME) && type.equals(V3_GEOM_COL_TYPE)) {
					return 3;
				}
				else if (name.equals(V4_GEOM_COL_NAME) && type.equals(V4_GEOM_COL_TYPE)) {
					return 4;
				}
			}
		} catch (SQLException e) {
			log.error("Error inspecting \"geometry_columns\" table", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// ignore
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					// ignore
				}
			}
		}

		return -1;
	}
}
