/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.postgresql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Supplier;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.geometry.DefaultGeometryProperty;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;
import eu.esdihumboldt.hale.common.instance.geometry.impl.WKTDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.jdbc.GeometryAdvisor;
import eu.esdihumboldt.hale.io.jdbc.constraints.GeometryMetadata;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;

/**
 * Geometry advisor for PostGIS.
 * 
 * @author Simon Templer, Dominik Reuter
 */
public class PostGISGeometries implements GeometryAdvisor<PGConnection> {

	@Override
	public boolean isFixedType(ColumnDataType columnType) {
		/*
		 * Concrete information on geometry type and SRS is not stored in the
		 * column but as metadata in the database. Therefore every column has to
		 * be configured on its own.
		 */
		return false;
	}

	@Override
	public Class<? extends Geometry> configureGeometryColumnType(PGConnection connection,
			Column column, DefaultTypeDefinition type) {
		Connection con = (Connection) connection;

		String columnValueName = column.getParent().getName();
		String geometryType = null;
		try {
			Statement stmt = con.createStatement();
			// Get the srid, dimension and geometry type
			ResultSet rs = stmt.executeQuery(
					"SELECT srid,type,coord_dimension FROM geometry_columns WHERE f_table_name = "
							+ "'" + columnValueName + "'");
			if (rs.next()) {
				geometryType = rs.getString("type");
				String dimension = rs.getString("coord_dimension");

				// Get the epsg code for the srid
				String srid = rs.getString("srid");
				ResultSet r = stmt.executeQuery(
						"SELECT auth_srid, auth_name, srtext FROM spatial_ref_sys WHERE srid = "
								+ srid);
				if (r.next()) {
					// Create Constraint to save the informations
					GeometryMetadata columnTypeConstraint = new GeometryMetadata(
							r.getString("auth_srid"), Integer.parseInt(dimension),
							r.getString("srtext"), r.getString("auth_name"));
					type.setConstraint(columnTypeConstraint);
				}
			}
			else {
				// XXX what if no SRID information is present? is that a case
				// that may still be valid?
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// In this case we have no geometry column information
		if (geometryType == null) {
			// use geometry even if no geometry column is present describing it
			return Geometry.class;
		}
		// return the geometryType
		if (geometryType.equalsIgnoreCase("MultiPolygon")) {
			return MultiPolygon.class;
		}
		else if (geometryType.equalsIgnoreCase("MultiPoint")) {
			return MultiPoint.class;
		}
		else if (geometryType.equalsIgnoreCase("MultiLineString")) {
			return MultiLineString.class;
		}
		// TODO: shouldn't this be LineString instead?
		else if (geometryType.equalsIgnoreCase("LinearRing")) {
			return LinearRing.class;
		}
		else if (geometryType.equalsIgnoreCase("Point")) {
			return Point.class;
		}
		else if (geometryType.equalsIgnoreCase("Polygon")) {
			return Polygon.class;
		}
		else {
			return Geometry.class;
		}
	}

	@Override
	public Object convertGeometry(GeometryProperty<?> geom, TypeDefinition columnType,
			PGConnection pgconn) throws Exception {

		PGgeometry pGeometry = null;
		// Transform from sourceCRS to targetCRS
		GeometryMetadata columnTypeMetadata = columnType.getConstraint(GeometryMetadata.class);

		// transform
		CoordinateReferenceSystem targetCRS = null;
		String authName = columnTypeMetadata.getAuthName();
		if (authName != null && authName.equals("EPSG")) {
			// PostGIS assumes lon/lat
			targetCRS = CRS.decode(authName + ":" + columnTypeMetadata.getSrs(), true);
		}
		else {
			String wkt = columnTypeMetadata.getSrsText();
			if (wkt != null && !wkt.isEmpty()) {
				targetCRS = CRS.parseWKT(wkt);
			}
		}

		Geometry targetGeometry;
		if (targetCRS != null) {
			MathTransform transform = CRS.findMathTransform(geom.getCRSDefinition().getCRS(),
					targetCRS);
			targetGeometry = JTS.transform(geom.getGeometry(), transform);
		}
		else {
			targetGeometry = geom.getGeometry();
		}

		// Convert the jts Geometry to postgis PGgeometry and set the SRSID
		pGeometry = new PGgeometry(targetGeometry.toText());
		try {
			pGeometry.getGeometry().setSrid(Integer.parseInt(columnTypeMetadata.getSrs()));
		} catch (Exception e) {
			// ignore
		}
		return pGeometry;
	}

	/**
	 * 
	 * @see eu.esdihumboldt.hale.io.jdbc.GeometryAdvisor#convertToInstanceGeometry(java.lang.Object,
	 *      eu.esdihumboldt.hale.common.schema.model.TypeDefinition,
	 *      java.lang.Object, java.util.function.Supplier)
	 */
	@Override
	public GeometryProperty<?> convertToInstanceGeometry(Object geom, TypeDefinition columnType,
			PGConnection connection, Supplier<CRSDefinition> crsProvider) throws Exception {

		if (geom instanceof PGgeometry) {
			PGgeometry pgeom = (PGgeometry) geom;

			// conversion to JTS via WKT
			// TODO use better conversion (p4b?)
			WKTReader2 reader = new WKTReader2();

			String value = pgeom.getGeometry().toString();
			if (value.startsWith(PGgeometry.SRIDPREFIX) && value.indexOf(';') >= 0) {
				value = value.substring(value.indexOf(';') + 1);
			}

			Geometry jtsGeom = reader.read(value);

			// determine CRS
			GeometryMetadata columnTypeMetadata = columnType.getConstraint(GeometryMetadata.class);
			CRSDefinition crsDef = null;
			String authName = columnTypeMetadata.getAuthName();
			if (authName != null && authName.equals("EPSG")) {
				// PostGIS assumes lon/lat order
				crsDef = new CodeDefinition(authName + ":" + columnTypeMetadata.getSrs(), true);
			}
			else {
				String wkt = columnTypeMetadata.getSrsText();
				if (wkt != null) {
					crsDef = new WKTDefinition(wkt, null);
				}
			}

			return new DefaultGeometryProperty<Geometry>(crsDef, jtsGeom);
		}

		throw new IllegalArgumentException("Only conversion of PGgeometry supported");
	}
}
