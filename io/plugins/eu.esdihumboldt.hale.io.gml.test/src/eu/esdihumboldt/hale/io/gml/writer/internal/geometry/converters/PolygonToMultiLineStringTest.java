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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Test {@link Polygon} to {@link MultiLineString} conversion
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@SuppressWarnings("restriction")
public class PolygonToMultiLineStringTest extends AbstractGeometryConverterTest {

	/**
	 * Test conversion with a simple box
	 */
	@Test
	public void testBox() {
		Coordinate[] coordinates = new Coordinate[5];
		coordinates[0] = coordinates[4] = new Coordinate(0, 0);
		coordinates[1] = new Coordinate(1, 0);
		coordinates[2] = new Coordinate(1, 1);
		coordinates[3] = new Coordinate(0, 1);
		LinearRing shell = geomFactory.createLinearRing(coordinates);
		Polygon poly = geomFactory.createPolygon(shell, null);

		PolygonToMultiLineString converter = new PolygonToMultiLineString();

		MultiLineString mls = converter.convert(poly);
		assertEquals("Expecting 4 lines", 4, mls.getNumGeometries()); //$NON-NLS-1$
		for (int i = 0; i < mls.getNumGeometries(); i++) {
			LineString segment = (LineString) mls.getGeometryN(i);
			assertEquals("Each line should have 2 points", 2, segment.getNumPoints()); //$NON-NLS-1$
			// first point
			assertEquals(coordinates[i], segment.getCoordinateN(0));
			// second point
			assertEquals(coordinates[i + 1], segment.getCoordinateN(1));
		}
	}

}
