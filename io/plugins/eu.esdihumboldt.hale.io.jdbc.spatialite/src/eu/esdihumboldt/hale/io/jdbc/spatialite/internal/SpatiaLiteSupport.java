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

import org.sqlite.SQLiteConnection;

/**
 * Interface for classes providing support for a specific version (or range of
 * versions) of SpatiaLite.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public interface SpatiaLiteSupport {

	/**
	 * Extract metadata about the geometry type of the specified geometry
	 * column.
	 * 
	 * @param conn connection to SpatiaLite DB
	 * @param tableName table name
	 * @param columnName column name
	 * @return properly initialized {@link GeometryTypeMetadata} instance
	 */
	public GeometryTypeMetadata getGeometryTypeMetadata(SQLiteConnection conn, String tableName,
			String columnName);

	/**
	 * Extract metadata about the specified Spatial Reference System.
	 * 
	 * @param conn connection to SpatiaLite DB
	 * @param srid Spatial Reference System ID
	 * @return properly initialized {@link SrsMetadata} instance
	 */
	public SrsMetadata getSrsMetadata(SQLiteConnection conn, int srid);

	/**
	 * Extract metadata about the specified Spatial Reference System.
	 * 
	 * @param conn connection to SpatiaLite DB
	 * @param auth the SRS authority
	 * @param code the SRS authority code
	 * @return properly initialized {@link SrsMetadata} instance
	 */
	public SrsMetadata getSrsMetadata(SQLiteConnection conn, String auth, int code);

}
