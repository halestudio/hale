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
import java.sql.Statement;

import org.sqlite.SQLiteConnection;

import org.locationtech.jts.geom.Geometry;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Base implementation of {@link SpatiaLiteSupport} interface.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class AbstractSpatiaLiteSupport implements SpatiaLiteSupport {

	private static final ALogger log = ALoggerFactory.getLogger(AbstractSpatiaLiteSupport.class);

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.spatialite.internal.SpatiaLiteSupport#getGeometryTypeMetadata(org.sqlite.SQLiteConnection,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public GeometryTypeMetadata getGeometryTypeMetadata(SQLiteConnection conn, String tableName,
			String columnName) {

		GeometryTypeMetadata meta = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = getGeometryTypeMetadataSQL();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, tableName);
			stmt.setString(2, columnName);

			rs = stmt.executeQuery();

			if (rs.next()) {
				int srid = rs.getInt("srid");
				Object type = rs.getObject("type");
				Object coordDim = rs.getObject("coord_dimension");

				meta = new GeometryTypeMetadata(srid, getGeometryType(type),
						getCoordDimensionAsInt(coordDim));
			}
		} catch (SQLException e) {
			String errMsg = String.format(
					"Error extracting metadata for geometry column \"%s\" in table \"%s\"",
					columnName, tableName);
			log.error(errMsg, e);
		} finally {
			closeFinally(stmt, rs);
		}

		return meta;
	}

	@Override
	public SrsMetadata getSrsMetadata(SQLiteConnection conn, int srid) {

		SrsMetadata meta = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			// String sqlMeta =
			// "SELECT auth_srid, auth_name, srs_wkt AS srtext FROM spatial_ref_sys WHERE srid = ?";
			String sqlMeta = getSrsMetadataSQL();
			stmt = conn.prepareStatement(sqlMeta);
			stmt.setInt(1, srid);
			rs = stmt.executeQuery();

			if (rs.next()) {
				int authSrid = rs.getInt("auth_srid");
				String authName = rs.getString("auth_name");
				String srText = rs.getString("srtext");

				meta = new SrsMetadata(srText, authSrid, authName, srid);
			}
		} catch (SQLException e) {
			String errMsg = String.format("Error extracting SRS metadata for SRID: %d", srid);
			log.error(errMsg, e);
		} finally {
			closeFinally(stmt, rs);
		}

		return meta;
	}

	@Override
	public SrsMetadata getSrsMetadata(SQLiteConnection conn, String auth, int code) {

		SrsMetadata meta = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sqlMeta = getSrsMetadataFromAuthSQL();
			stmt = conn.prepareStatement(sqlMeta);
			stmt.setString(1, auth);
			stmt.setInt(2, code);
			rs = stmt.executeQuery();

			if (rs.next()) {
				int authSrid = rs.getInt("auth_srid");
				String authName = rs.getString("auth_name");
				String srText = rs.getString("srtext");
				int srid = rs.getInt("srid");

				meta = new SrsMetadata(srText, authSrid, authName, srid);
			}
		} catch (SQLException e) {
			String errMsg = String.format("Error extracting SRS metadata for SRS: %s:%d", auth,
					code);
			log.error(errMsg, e);
		} finally {
			closeFinally(stmt, rs);
		}

		return meta;
	}

	/**
	 * Return a SpatiaLite version dependent SQL query selecting at least three
	 * columns: <code>srid</code> (integer), <code>type</code> (any type) and
	 * <code>coord_dimension</code> (any type).
	 * <p>
	 * Template method, implementation is provided by subclasses.
	 * </p>
	 * 
	 * @return SQL statement to extract geometry type metadata from the
	 *         <code>geometry_columns</code> metadata table
	 */
	protected abstract String getGeometryTypeMetadataSQL();

	/**
	 * Convert the retrieved <code>coord_dimension</code> column value to an
	 * integer.
	 * <p>
	 * Template method, implementation is provided by subclasses.
	 * </p>
	 * 
	 * @param coordDimension the value of the <code>coord_dimension</code>
	 *            column
	 * @return the dimensionality of the geometry coordinates (either 2, 3, 4 or
	 *         -1 in case of error)
	 */
	protected abstract int getCoordDimensionAsInt(Object coordDimension);

	/**
	 * Map the retrieved <code>type</code> column value to a subclass of
	 * {@link Geometry}.
	 * <p>
	 * Template method, implementation is provided by subclasses.
	 * </p>
	 * 
	 * @param type the value of the <code>type</code> column
	 * @return the corresponding JTS {@link Geometry} subclass
	 */
	protected abstract Class<? extends Geometry> getGeometryType(Object type);

	/**
	 * Return a SpatiaLite version dependent SQL query selecting at least three
	 * columns: <code>auth_srid</code> (integer), <code>auth_name</code>
	 * (string) and <code>srtext</code> (string).
	 * <p>
	 * Template method, implementation is provided by subclasses.
	 * </p>
	 * 
	 * @return SQL statement to extract SRS metadata from the
	 *         <code>spatial_ref_sys</code> metadata table
	 */
	protected abstract String getSrsMetadataSQL();

	/**
	 * Return a SpatiaLite version dependent SQL query selecting at least three
	 * columns: <code>auth_srid</code> (integer), <code>auth_name</code>
	 * (string), <code>srtext</code> (string) and <code>srid</code> (integer).
	 * <p>
	 * Template method, implementation is provided by subclasses.
	 * </p>
	 * 
	 * @return SQL statement to extract SRS metadata from the
	 *         <code>spatial_ref_sys</code> metadata table
	 */
	protected abstract String getSrsMetadataFromAuthSQL();

	/**
	 * Utility method to close a {@link ResultSet} and a {@link Statement}
	 * instance.
	 * 
	 * @param stmt the {@link Statement} to close
	 * @param rs the {@link ResultSet} to close
	 */
	protected void closeFinally(Statement stmt, ResultSet rs) {
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

}
