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

package eu.esdihumboldt.hale.io.gml.geometry.handler;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPoint;

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
 * Test for reading envelope geometries
 * 
 * @author Patrick Lieb
 * @author Arun Varma
 */
@Features("Geometries")
@Stories("GML")
public class EnvelopeHandlerTest extends AbstractHandlerTest {

	// XXX shouldn't an envelope rather be represented by a polygon?
	private MultiPoint reference;

	@Override
	public void init() {
		super.init();

		Coordinate[] coordinates = new Coordinate[] {
				new Coordinate(-39799.68820381, 273207.53980172),
				new Coordinate(-39841.185, 273182.863) };
		reference = geomFactory.createMultiPoint(coordinates);
	}

	/**
	 * Test envelope geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testEnvelopeGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/envelope/sample-envelope-gml3.xml").toURI());

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. EnvelopeProperty defined through pos
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSingleGeometry(instance, referenceChecker(reference));
		} finally {
			it.close();
		}
	}

	/**
	 * Test envelope geometries read from a GML 3 file
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testEnvelopeGml3_Grid() throws Exception {
		ReaderConfiguration config = InterpolationConfigurations.ALL_TO_GRID_DEFAULT;
		InstanceCollection instances = AbstractHandlerTest.loadXMLInstances(
				getClass().getResource("/data/gml/geom-gml3.xsd").toURI(),
				getClass().getResource("/data/envelope/sample-envelope-gml3.xml").toURI(), config);

		// one instances expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// 1. EnvelopeProperty defined through pos
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();

			checkSingleGeometry(instance,
					combine(referenceChecker(reference,
							InterpolationHelper.DEFAULT_MAX_POSITION_ERROR),
					config.geometryChecker()));
		} finally {
			it.close();
		}
	}

}
