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
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * {@link SpatiaLiteSupport} implementation supporting SpatiaLite version 4.0.0
 * or higher.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class SpatiaLiteSupportVersion4 extends AbstractSpatiaLiteSupport {

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.spatialite.internal.AbstractSpatiaLiteSupport#getGeometryTypeMetadataSQL()
	 */
	@Override
	protected String getGeometryTypeMetadataSQL() {
		return "SELECT srid, geometry_type AS type, coord_dimension FROM geometry_columns WHERE f_table_name = Lower(?) AND f_geometry_column = Lower(?)";
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.spatialite.internal.AbstractSpatiaLiteSupport#getSrsMetadataSQL()
	 */
	@Override
	protected String getSrsMetadataSQL() {
		return "SELECT auth_srid, auth_name, srtext FROM spatial_ref_sys WHERE srid = ?";
	}

	@Override
	protected String getSrsMetadataFromAuthSQL() {
		return "SELECT auth_srid, auth_name, srtext, srid FROM spatial_ref_sys WHERE auth_name = ? AND auth_srid = ?";
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.spatialite.internal.AbstractSpatiaLiteSupport#getGeometryType(java.lang.Object)
	 */
	@Override
	protected Class<? extends Geometry> getGeometryType(Object type) {
		Integer geomType = Integer.class.cast(type);

		Class<? extends Geometry> geometryClass = null;
		int geomSelector = geomType % 1000;

		switch (geomSelector) {
		case 1:
			geometryClass = Point.class;
			break;
		case 2:
			geometryClass = LineString.class;
			break;
		case 3:
			geometryClass = Polygon.class;
			break;
		case 4:
			geometryClass = MultiPoint.class;
			break;
		case 5:
			geometryClass = MultiLineString.class;
			break;
		case 6:
			geometryClass = MultiPolygon.class;
			break;
		case 7:
			geometryClass = GeometryCollection.class;
			break;
		case 0:
			geometryClass = Geometry.class;
			break;
		default:
			// should never happen
			break;
		}

		return geometryClass;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.jdbc.spatialite.internal.AbstractSpatiaLiteSupport#getCoordDimensionAsInt(java.lang.Object)
	 */
	@Override
	protected int getCoordDimensionAsInt(Object coordDimension) {
		// TODO: consider deriving this from geometry type
		return Integer.class.cast(coordDimension);
	}

}
