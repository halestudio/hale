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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.sqlite.SQLiteConnection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Helper for SpatiaLite.
 * 
 * @author Simon Templer
 */
public class SpatiaLiteHelper {

	private static final ALogger log = ALoggerFactory.getLogger(SpatiaLiteHelper.class);

	/**
	 * Determine if the SpatiaLite module is loaded.
	 * 
	 * @param connection the database connection
	 * @return <code>true</code> if the SpatiaLite module is loaded,
	 *         <code>false</code> if it is not or the status cannot be
	 *         determined
	 */
	private static boolean isSpatiaLiteLoaded(SQLiteConnection connection) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("SELECT spatialite_version();");
		} catch (SQLException e1) {
			// function not available
			return false;
		}
		try {
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				@SuppressWarnings("unused")
				String spatiaLiteVersion = result.getString(1);
				return true;
			}
			else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				// ignore
			}
		}

	}

	private static final Cache<SQLiteConnection, Boolean> loadedCache = CacheBuilder.newBuilder()
			.weakKeys().build();

	/**
	 * Determine if SpatiaLite is loaded for the given connection. If not,
	 * reports to the user about the misconfiguration. A report is done only
	 * once per unique connection.
	 * 
	 * @param connection the database connection
	 * @param error if the report should be an error, otherwise it is a warning
	 * @return <code>true</code> if the SpatiaLite module is loaded,
	 *         <code>false</code> if it is not or the status cannot be
	 *         determined
	 */
	public static boolean isSpatialLiteLoadedReport(final SQLiteConnection connection, boolean error) {
		try {
			Boolean result = loadedCache.getIfPresent(connection);
			if (result != null) {
				return result;
			}
			result = loadedCache.get(connection, new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return isSpatiaLiteLoaded(connection);
				}
			});
			if (!result) {
				String msg = "The SpatiaLite extension for SQLite (mod_spatialite) could not be loaded. "
						+ "This means that it is not possible to properly load or store geometries from/to SQLite.\n\n"
						+ "Please check the hale studio help on how to make it available:\n"
						+ "hale studio User Guide > Reference > Supported formats > Import > SQLite and SpatiaLite";
				if (error) {
					log.userError(msg);
				}
				else {
					log.userWarn(msg);
				}
			}
			return result;
		} catch (ExecutionException e) {
			log.error("Error determining if SpatiaLite is loaded", e);
			// state that it is not loaded
			return false;
		}
	}

}
