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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * {@link SpatiaLiteSupport} implementation supporting SpatiaLite version 3.1.0
 * or lower.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteSupportVersion3 extends AbstractSpatiaLiteSupport {

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.spatialite.internal.AbstractSpatiaLiteSupport#getGeometryTypeMetadataSQL()
	 */
	@Override
	protected String getGeometryTypeMetadataSQL() {
		return "SELECT srid, type, coord_dimension FROM geometry_columns WHERE f_table_name = ? AND f_geometry_column = ?";
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.spatialite.internal.AbstractSpatiaLiteSupport#getSrsMetadataSQL()
	 */
	@Override
	protected String getSrsMetadataSQL() {
		return "SELECT auth_srid, auth_name, srs_wkt AS srtext FROM spatial_ref_sys WHERE srid = ?";
	}

	@Override
	protected String getSrsMetadataFromAuthSQL() {
		return "SELECT auth_srid, auth_name, srs_wkt AS srtext, srid FROM spatial_ref_sys WHERE auth_name = ? AND auth_srid = ?";
	}

	@Override
	protected int getCoordDimensionAsInt(Object coordDimension) {
		String coordDimensionAsString = String.class.cast(coordDimension);

		if ("XY".equals(coordDimensionAsString)) {
			return 2;
		}
		else if ("XYZ".equals(coordDimensionAsString) || "XYM".equals(coordDimensionAsString)) {
			return 3;
		}
		else if ("XYZM".equals(coordDimensionAsString)) {
			return 4;
		}
		else {
			// should never happen
			return -1;
		}
	}

	@Override
	protected Class<? extends Geometry> getGeometryType(Object type) {
		String geomType = String.class.cast(type);

		if ("MultiPoint".equalsIgnoreCase(geomType)) {
			return MultiPoint.class;
		}
		else if ("MultiLineString".equalsIgnoreCase(geomType)) {
			return MultiLineString.class;
		}
		else if ("MultiPolygon".equalsIgnoreCase(geomType)) {
			return MultiPolygon.class;
		}
		else if ("Point".equalsIgnoreCase(geomType)) {
			return Point.class;
		}
		else if ("LineString".equalsIgnoreCase(geomType)) {
			return LinearRing.class;
		}
		else if ("Polygon".equalsIgnoreCase(geomType)) {
			return Polygon.class;
		}
		else if ("GeometryCollection".equalsIgnoreCase(geomType)) {
			return GeometryCollection.class;
		}
		else if ("Geometry".equalsIgnoreCase(geomType)) {
			return Geometry.class;
		}
		else {
			// should never happen
			return null;
		}
	}

}
