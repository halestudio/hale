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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryConverter;

/**
 * Converts a {@link Polygon} to a {@link MultiLineString}.
 * 
 * The polygon is divided into multiple LineStrings, each containing two points.
 * Needed for polygons that represent curves.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class MultiPolygonToMultiLineString
		extends AbstractGeometryConverter<MultiPolygon, MultiLineString> {

	private final PolygonToLineString polygonConverter = new PolygonToLineString();

	/**
	 * Default constructor
	 */
	public MultiPolygonToMultiLineString() {
		super(MultiPolygon.class, MultiLineString.class);
	}

	/**
	 * @see GeometryConverter#convert(Geometry)
	 */
	@Override
	public MultiLineString convert(MultiPolygon polygon) {
		List<LineString> lineStrings = new ArrayList<>();

		for (int i = 0; i < polygon.getNumGeometries(); i++) {
			Geometry g = polygon.getGeometryN(i);
			if (g instanceof Polygon) {
				lineStrings.add(polygonConverter.convert((Polygon) g));
			}
		}

		return geomFactory
				.createMultiLineString(lineStrings.toArray(new LineString[lineStrings.size()]));
	}

	/**
	 * @see GeometryConverter#lossOnConversion(Geometry)
	 */
	@Override
	public boolean lossOnConversion(MultiPolygon geometry) {
		// we classify the conversion as a loss because it's a change from a
		// surface to a curve and the interior is lost
		return true;
	}

}
