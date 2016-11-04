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
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryConverter;

/**
 * Converts a {@link Polygon} to a {@link LineString}.
 * 
 * The polygon is divided into multiple LineStrings, each containing two points.
 * Needed for polygons that represent curves.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class PolygonToLineString extends AbstractGeometryConverter<Polygon, LineString> {

	/**
	 * Default constructor
	 */
	public PolygonToLineString() {
		super(Polygon.class, LineString.class);
	}

	/**
	 * @see GeometryConverter#convert(Geometry)
	 */
	@Override
	public LineString convert(Polygon polygon) {
		LineString exterior = polygon.getExteriorRing();

		// the previously implemented behavior of creating a MultiLineString
		// forming different segments causes problems due to the potentially
		// massive amount of segments

		Coordinate[] coordinates = exterior.getCoordinates();
		int length = coordinates.length;
		if (length > 1) {
			// test if first equals last
			boolean isRing = coordinates[0].equals(coordinates[length - 1]);
			if (isRing) {
				return exterior;
			}
			else {
				// create a ring line string
				Coordinate[] org = exterior.getCoordinates();
				Coordinate[] copy = new Coordinate[org.length + 1];
				System.arraycopy(org, 0, copy, 0, org.length);
				copy[org.length] = org[0];
				return geomFactory.createLineString(copy);
			}
		}
		else {
			return exterior;
		}
	}

	/**
	 * @see GeometryConverter#lossOnConversion(Geometry)
	 */
	@Override
	public boolean lossOnConversion(Polygon geometry) {
		// we classify the conversion as a loss because it's a change from a
		// surface to a curve and the interior is lost
		return true;
	}

}
