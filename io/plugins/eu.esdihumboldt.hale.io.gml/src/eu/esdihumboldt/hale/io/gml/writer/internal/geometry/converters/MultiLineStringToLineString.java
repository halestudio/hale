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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Converts {@link MultiPolygon}s to {@link Polygon}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class MultiLineStringToLineString extends
		AbstractGeometryCollectionConverter<MultiLineString, LineString> {

	/**
	 * Default constructor
	 */
	public MultiLineStringToLineString() {
		super(MultiLineString.class, LineString.class);
	}

	/**
	 * @see AbstractGeometryCollectionConverter#createEmptyGeometry()
	 */
	@Override
	protected LineString createEmptyGeometry() {
		return geomFactory.createLineString((Coordinate[]) null);
	}

}
