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

package eu.esdihumboldt.hale.io.gml.geometry.handler.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.BeforeClass;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.geometry.GeometryFinder;
import eu.esdihumboldt.hale.common.instance.helper.BreadthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;
import eu.esdihumboldt.util.svg.test.AbstractSVGPainterTest;

/**
 * Base class for handler tests.
 * 
 * @author Simon Templer
 * @author Arun Varma
 */
@SuppressWarnings({ "restriction", "deprecation" })
public abstract class AbstractHandlerTest extends AbstractSVGPainterTest {

	/**
	 * Test namespace
	 */
	public static final String NS_TEST = "eu:esdihumboldt:hale:test";

	/**
	 * The geometry factory instance
	 */
	protected GeometryFactory geomFactory;

	/**
	 * Prepare the conversion service
	 */
	@BeforeClass
	public static void initAll() {
		TestUtil.startConversionService();
	}

	/**
	 * Initialize the test class.
	 */
	@Before
	public void init() {
		geomFactory = new GeometryFactory();

		PropertyResolver.clearCache();
	}

	/**
	 * Load an instance collection from a GML file.
	 * 
	 * @param schemaLocation the GML application schema location
	 * @param xmlLocation the GML file location
	 * @return the instance collection
	 * @throws IOException if reading schema or instances failed
	 * @throws IOProviderConfigurationException if the I/O providers were not
	 *             configured correctly
	 */
	public static InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation)
			throws IOException, IOProviderConfigurationException {
		return loadXMLInstances(schemaLocation, xmlLocation, null);
	}

	/**
	 * Load an instance collection from a GML file.
	 * 
	 * @param schemaLocation the GML application schema location
	 * @param xmlLocation the GML file location
	 * @param interpolConfig the interpolation configuration
	 * @return the instance collection
	 * @throws IOException if reading schema or instances failed
	 * @throws IOProviderConfigurationException if the I/O providers were not
	 *             configured correctly
	 */
	public static InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation,
			@Nullable ReaderConfiguration interpolConfig)
					throws IOException, IOProviderConfigurationException {
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(schemaLocation));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		Schema sourceSchema = reader.getSchema();

		InstanceReader instanceReader = new GmlInstanceReader();

		instanceReader.setSource(new DefaultInputSupplier(xmlLocation));
		instanceReader.setSourceSchema(sourceSchema);
		if (interpolConfig != null) {
			interpolConfig.apply(instanceReader);
		}

		IOReport instanceReport = instanceReader.execute(null);
		assertTrue(instanceReport.isSuccess());

		return instanceReader.getInstances();
	}

	/**
	 * Creates a geometry checker that checks for equality with a reference
	 * geometry.
	 * 
	 * @param referenceGeometry the referenced geometry
	 * @return the checker
	 */
	protected Consumer<Geometry> referenceChecker(Geometry referenceGeometry) {
		return (geom) -> {
			assertTrue("Geometry differs from reference geometry",
					geom.equalsExact(referenceGeometry));
		};
	}

	/**
	 * Creates a geometry checker that checks for equality with a reference
	 * geometry.
	 * 
	 * @param referenceGeometry the referenced geometry
	 * @param tolerance distance at or below which two Coordinates are
	 *            considered equal
	 * @return the checker
	 */
	protected Consumer<Geometry> referenceChecker(Geometry referenceGeometry, double tolerance) {
		return (geom) -> {
			assertTrue("Geometry differs from reference geometry",
					geom.equalsExact(referenceGeometry, tolerance));
		};
	}

	/**
	 * Check a single geometry contained in an instance (at an arbitrary path).
	 * 
	 * @param instance the geometry instance
	 * @param checker the checker (should throw an exception when the check
	 *            fails)
	 * @return the collection of encountered geometries
	 */
	protected Collection<GeometryProperty<?>> checkSingleGeometry(Instance instance,
			@Nullable Consumer<Geometry> checker) {
		GeometryFinder finder = new GeometryFinder(null);
		BreadthFirstInstanceTraverser traverser = new BreadthFirstInstanceTraverser();
		traverser.traverse(instance, finder);
		List<GeometryProperty<?>> geoms = finder.getGeometries();
		assertFalse("No geometry found in instances", geoms.isEmpty());
		assertEquals("More than one geometry found in instance", 1, geoms.size());
		Geometry geom = geoms.get(0).getGeometry();

		if (checker != null) {
			checker.accept(geom);
		}

		return geoms;
	}

	/**
	 * Create a combined geometry checker.
	 * 
	 * @param checkers the checkers to combine
	 * @return the combined checker executing all checks
	 */
	@SafeVarargs
	protected final Consumer<Geometry> combine(Consumer<Geometry>... checkers) {
		return (geom) -> {
			for (Consumer<Geometry> checker : checkers) {
				checker.accept(geom);
			}
		};
	}

	/**
	 * Create a geometry checker that makes sure there are no neighboring
	 * coordinates that are the same. This is only a simple test based on the
	 * overall sequence of coordinates of the geometry
	 * 
	 * @return the geometry checker
	 */
	protected Consumer<Geometry> noCoordinatePairs() {
		return (geom) -> {
			Coordinate[] coord = geom.getCoordinates();
			for (int i = 0; i < coord.length - 1; i++) {
				Coordinate c1 = coord[i];
				Coordinate c2 = coord[i + 1];
				assertNotEquals("Neighboring coordinates are the same", c1, c2);
			}
		};
	}

}
