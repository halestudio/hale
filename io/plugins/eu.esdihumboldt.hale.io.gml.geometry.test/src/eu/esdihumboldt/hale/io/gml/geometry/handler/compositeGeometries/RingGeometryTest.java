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

package eu.esdihumboldt.hale.io.gml.geometry.handler.compositeGeometries;

import static org.junit.Assert.assertTrue;

import java.util.function.Consumer;

import org.junit.Test;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;

import eu.esdihumboldt.hale.common.instance.geometry.InterpolationHelper;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.InterpolationConfigurations;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.ReaderConfiguration;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test for reading ring geometries
 * 
 * @author Patrick Lieb
 * @author Arun Varma
 * @author Simon Templer
 */
@Features("Geometries")
@Stories("GML")
public class RingGeometryTest extends AbstractHandlerTest {

	private LinearRing reference;
	private final ReaderConfiguration gridConfig = InterpolationConfigurations.ALL_TO_GRID_DEFAULT;
	private Consumer<Geometry> checker;
	private Consumer<Geometry> gridChecker;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2), new Coordinate(0.01, 3.2) };
		LineString linestring = geomFactory.createLineString(coordinates);

		reference = geomFactory.createLinearRing(linestring.getCoordinates());

		// for grid test
		coordinates = new Coordinate[] { new Coordinate(0, 3.2), new Coordinate(3.3, 3.3),
				new Coordinate(0, -3.2), new Coordinate(0, 3.2) };
		linestring = geomFactory.createLineString(coordinates);

		checker = combine(noCoordinatePairs(), referenceChecker(reference));

		gridChecker = combine(noCoordinatePairs(),
				referenceChecker(reference, InterpolationHelper.DEFAULT_MAX_POSITION_ERROR),
				gridConfig.geometryChecker());
	}

	/**
	 * Test ring geometries read from a GML 2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testRingGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/sample-ring-gml31.xml").toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// Ring with segments defined through LineStringSegment
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test ring geometries read from a GML 2 file. Geometry coordinates will be
	 * moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testRingGml31_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml31.xsd").toURI(),
				getClass().getResource("/data/sample-ring-gml31.xml").toURI(), gridConfig);

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// Ring with segments defined through LineStringSegment
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

}
