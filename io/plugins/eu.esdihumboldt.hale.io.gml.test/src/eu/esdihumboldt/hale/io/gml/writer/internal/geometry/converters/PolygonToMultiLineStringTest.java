/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
