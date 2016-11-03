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
 */
@SuppressWarnings("restriction")
public class PolygonToLineStringTest extends AbstractGeometryConverterTest {

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

		PolygonToLineString converter = new PolygonToLineString();

		LineString mls = converter.convert(poly);
		assertEquals("Expecting 1 lines", 1, mls.getNumGeometries()); //$NON-NLS-1$
		assertEquals("Should have 5 points", 5, mls.getNumPoints()); //$NON-NLS-1$
		// first point
		assertEquals(coordinates[0], mls.getCoordinateN(0));
		// second point
		assertEquals(coordinates[0], mls.getCoordinateN(4));
	}

}
