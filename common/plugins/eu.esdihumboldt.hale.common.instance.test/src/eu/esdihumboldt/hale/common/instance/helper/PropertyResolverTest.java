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

package eu.esdihumboldt.hale.common.instance.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.osgi.util.OsgiUtils.Condition;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceReader;
import eu.esdihumboldt.hale.io.gml.reader.internal.StreamGmlReader;
import eu.esdihumboldt.hale.io.gml.reader.internal.XmlInstanceReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Tests for {@link PropertyResolver}
 * 
 * @author Sebastian Reinhardt
 */
@SuppressWarnings("restriction")
public class PropertyResolverTest {

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		assertTrue("Conversion service not available", OsgiUtils.waitUntil(new Condition() {

			@Override
			public boolean evaluate() {
				return HalePlatform.getService(ConversionService.class) != null;
			}
		}, 30));
	}

	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadShiporder() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/shiporder/shiporder.xsd").toURI(), getClass()
						.getResource("/data/shiporder/shiporder.xml").toURI());

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			@SuppressWarnings("unused")
			TypeDefinition test = instance.getDefinition().getChildren().iterator().next()
					.asProperty().getParentType();

			assertTrue(PropertyResolver
					.hasProperty(instance, "{http://www.example.com}orderperson"));

			assertTrue(PropertyResolver.hasProperty(instance,
					"{http://www.example.com}shipto.{http://www.example.com}city"));
			assertTrue(PropertyResolver.getQueryPath(instance,
					"{http://www.example.com}shipto.{http://www.example.com}city").contains(
					"{http://www.example.com}shipto.{http://www.example.com}city"));

			assertTrue(PropertyResolver.hasProperty(instance, "orderperson"));
			assertTrue(PropertyResolver.hasProperty(instance, "shipto.city"));
			assertTrue(PropertyResolver
					.hasProperty(instance, "shipto.{http://www.example.com}city"));

			assertEquals(PropertyResolver.getValues(instance, "shipto.city").iterator().next(),
					"4000 Stavanger");
		} finally {
			it.close();
		}
	}

	/**
	 * Test with a wrapper instance that has no definition itself.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadShiporderWrapped() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/data/shiporder/shiporder.xsd").toURI(), getClass()
						.getResource("/data/shiporder/shiporder.xml").toURI());

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			// create dummy instance
			MutableInstance wrapperInstance = new DefaultInstance(null, null);
			wrapperInstance.addProperty(new QName("value"), instance);

			assertEquals(PropertyResolver.getValues(wrapperInstance, "value.shipto.city")
					.iterator().next(), "4000 Stavanger");
		} finally {
			it.close();
		}
	}

	/**
	 * Test with complex instances.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Ignore
	public void testComplexInstances() throws Exception {

		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier((getClass().getResource(
				"/data/erm/inspire3/HydroPhysicalWaters.xsd").toURI())));
		IOReport report = reader.execute(null);
		assertTrue(report.isSuccess());
		Schema schema = reader.getSchema();

		StreamGmlReader instanceReader = new GmlInstanceReader();
		instanceReader.setSource(new DefaultInputSupplier(getClass().getResource(
				"/data/out/transformWrite_ERM_HPW.gml").toURI()));
		instanceReader.setSourceSchema(schema);

		instanceReader.validate();
		report = instanceReader.execute(null);
		assertTrue(report.isSuccess());

		InstanceCollection instances = instanceReader.getInstances();
		assertFalse(instances.isEmpty());

		ResourceIterator<Instance> ri = instances.iterator();
		try {
			Instance instance = ri.next();

			assertTrue(PropertyResolver.hasProperty(instance, "description"));
			assertTrue(PropertyResolver.hasProperty(instance,
					"{http://www.opengis.net/gml/3.2}description"));
			assertTrue(PropertyResolver.hasProperty(instance, "boundedBy.Envelope.coordinates"));
			assertTrue(PropertyResolver.hasProperty(instance,
					"boundedBy.Envelope.{http://www.opengis.net/gml/3.2}coordinates"));
			assertTrue(PropertyResolver
					.hasProperty(
							instance,
							"{http://www.opengis.net/gml/3.2}boundedBy.{http://www.opengis.net/gml/3.2}Envelope.{http://www.opengis.net/gml/3.2}coordinates"));
			assertFalse(PropertyResolver.hasProperty(instance,
					"boundedBy.Envelope.{http://www.opengis.net/gml/3.2}coordinates.description"));
			assertTrue(PropertyResolver.hasProperty(instance, "location.AbstractSolid.id"));

			assertTrue(PropertyResolver.hasProperty(instance,
					"location.CompositeCurve.curveMember.CompositeCurve.curveMember.type"));
			assertTrue(PropertyResolver
					.hasProperty(
							instance,
							"{http://www.opengis.net/gml/3.2}location.{http://www.opengis.net/gml/3.2}CompositeCurve.{http://www.opengis.net/gml/3.2}curveMember.{http://www.opengis.net/gml/3.2}CompositeCurve.{http://www.opengis.net/gml/3.2}curveMember.type"));
			assertTrue(PropertyResolver
					.hasProperty(
							instance,
							"{http://www.opengis.net/gml/3.2}location.CompositeCurve.{http://www.opengis.net/gml/3.2}curveMember.{http://www.opengis.net/gml/3.2}CompositeCurve.curveMember.type"));

			assertEquals("EPSG:4326",
					PropertyResolver.getValues(instance, "geometry.Polygon.srsName").iterator()
							.next().toString());

			// TODO
		} finally {
			ri.close();
		}
	}

	private InstanceCollection loadXMLInstances(URI schemaLocation, URI xmlLocation)
			throws IOException, IOProviderConfigurationException {
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(schemaLocation));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		Schema sourceSchema = reader.getSchema();

		//

		InstanceReader instanceReader = new XmlInstanceReader();
		instanceReader.setParameter(XmlInstanceReader.PARAM_IGNORE_ROOT, Value.of(false));

		instanceReader.setSource(new DefaultInputSupplier(xmlLocation));
		instanceReader.setSourceSchema(sourceSchema);

		IOReport instanceReport = instanceReader.execute(null);
		assertTrue(instanceReport.isSuccess());

		return instanceReader.getInstances();
	}

}

//problem? set *spring*osgi and convert to true in runconfigs->plugins
