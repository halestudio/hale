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
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

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
 * Test for reading triangle geometries
 * 
 * @author Patrick Lieb, Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class TriangleGeometryTest extends AbstractHandlerTest {

	private Polygon referencePolygon;
	private final ReaderConfiguration gridConfig = InterpolationConfigurations.ALL_TO_GRID_DEFAULT;
	private Consumer<Geometry> checker;
	private Consumer<Geometry> gridChecker;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] { new Coordinate(0.01, 3.2),
				new Coordinate(3.33, 3.33), new Coordinate(0.01, -3.2), new Coordinate(-3.33, -3.2),
				new Coordinate(0.01, 3.2) };

		LinearRing linearRing = geomFactory.createLinearRing(coordinates);
		referencePolygon = geomFactory.createPolygon(linearRing, null);

		checker = combine(noCoordinatePairs(), referenceChecker(referencePolygon));

		gridChecker = combine(noCoordinatePairs(),
				referenceChecker(referencePolygon, InterpolationHelper.DEFAULT_MAX_POSITION_ERROR),
				gridConfig.geometryChecker());
	}

	/**
	 * Test triangle geometries read from a GML 3.2 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testTriangleGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/sample-triangle-gml32.xml").toURI());

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, checker);

			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, checker);
		} finally {
			it.close();
		}
	}

	/**
	 * Test triangle geometries read from a GML 3.2 file. Geometry coordinates
	 * will be moved to the universal grid
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testTriangleGml32_Grid() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml32.xsd").toURI(),
				getClass().getResource("/data/sample-triangle-gml32.xml").toURI(), gridConfig);

		// two instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, gridChecker);

			// 1. segments with LineStringSegment defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			instance = it.next();
			checkSingleGeometry(instance, gridChecker);
		} finally {
			it.close();
		}
	}

}
