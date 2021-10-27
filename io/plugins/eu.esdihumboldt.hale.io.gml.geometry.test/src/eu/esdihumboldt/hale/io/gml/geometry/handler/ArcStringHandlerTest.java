/*
 * Copyright (c) 2021 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.gml.geometry.handler;

import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for interpolating ArcString geometries
 * 
 * @author Johanna Ott
 */
public class ArcStringHandlerTest extends AbstractHandlerTest {

	private Consumer<Geometry> checker;
	private LineString reference;

	@Override
	public void init() {
		super.init();

		// define expected values
		Coordinate start = new Coordinate(552400.131, 5941061.83);
		Coordinate end = new Coordinate(551735.79, 5941770.827);
		int coordinateCount = 16385;

		checker = combine(startEndChecker(start, end, coordinateCount), noCoordinatePairs());

	}

	/**
	 * Test linestring geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testArcStringGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/arcstring/sample-arcstring-gml32.xml").toURI());

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue("ArcString is not interpolated correctly", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}
}
