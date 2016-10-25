/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.jdbc.mssql.util;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Cache for SRS codes
 * 
 * @author Arun
 */
public abstract class SRSUtil {

	private static Map<Integer, SRS> cache = new HashMap<>();
	private static final ALogger log = ALoggerFactory.getLogger(SRSUtil.class);

	/**
	 * get SRS code. First find in cache, if unsuccessful then get from database
	 * and also store in cache.
	 * 
	 * @param srId a spatial reference id attached to object
	 * @param con A {@link SQLServerConnection} object
	 * @return String value of SRS code or <code>null</code> if absent in cache
	 *         and fail to load from database
	 */
	public static String getSRS(int srId, SQLServerConnection con) {
		if (cache.get(srId) == null) {
			if (!getFromDatabase(srId, con))
				return null;
		}
		return cache.get(srId).getAuthorizedSrId();
	}

	/**
	 * get an authority name. First find in cache, if unsuccessful then get from
	 * database and also store in cache.
	 * 
	 * @param srId a spatial reference id attached to object
	 * @param con A {@link SQLServerConnection} object
	 * @return String value of authority name or <code>null</code> if absent in
	 *         cache and fail to load from database
	 */
	public static String getAuthorizedName(int srId, SQLServerConnection con) {
		if (cache.get(srId) == null) {
			if (!getFromDatabase(srId, con))
				return null;
		}
		return cache.get(srId).getAuthorityName();
	}

	/**
	 * get SRS text. First find in cache, if unsuccessful then get from database
	 * and also store in cache.
	 * 
	 * @param srId a spatial reference id attached to object
	 * @param con A {@link SQLServerConnection} object
	 * @return String value of authority name or <code>null</code> if absent in
	 *         cache and fail to load from database
	 */
	public static String getSRSText(int srId, SQLServerConnection con) {
		if (cache.get(srId) == null) {
			if (!getFromDatabase(srId, con))
				return null;
		}
		return cache.get(srId).getSrsText();
	}

	/**
	 * Fetch SRS information from database
	 * 
	 * @param srId A spatial reference id
	 * @param con A {@link SQLServerConnection} object
	 * @return true if successful else false.
	 */
	private static boolean getFromDatabase(int srId, SQLServerConnection con) {
		try {

			String sqlSRS = "SELECT [authorized_spatial_reference_id] ,[authority_name] ,[well_known_text] " //
					+ "FROM [sys].[spatial_reference_systems] Where [spatial_reference_id]=" + srId; //

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sqlSRS);

			// put in cache
			if (rs.next())
				cache.put(srId,
						new SRS(Integer.toString(rs.getInt(1)), rs.getString(2), rs.getString(3)));
			else
				return false;

			return true;
		} catch (Exception ex) {
			log.error("Could not get SRS from database for spatial reference id " + srId, ex);
			return false;
		}
	}

}
