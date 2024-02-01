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

package eu.esdihumboldt.hale.common.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import javax.xml.namespace.QName;

import org.geotools.filter.text.cql2.CQLException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.DefaultInstance;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceReader;
import eu.esdihumboldt.hale.io.gml.reader.internal.StreamGmlReader;
import eu.esdihumboldt.hale.io.gml.reader.internal.XmlInstanceReader;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeSchemaReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * TODO Type description
 * 
 * @author Basti
 */
@SuppressWarnings({ "restriction", "javadoc" })
public class FilterTest {

	static InstanceCollection complexinstances;
	static boolean init = false;

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	@Before
	public void loadXML() throws Exception {
		if (init == false) {
			SchemaReader reader = new XmlSchemaReader();
			reader.setSharedTypes(null);
			reader.setSource(new DefaultInputSupplier((getClass()
					.getResource("/testdata/inspire3/HydroPhysicalWaters.xsd").toURI())));
			IOReport report = reader.execute(null);
			assertTrue(report.isSuccess());
			Schema schema = reader.getSchema();

			StreamGmlReader instanceReader = new GmlInstanceReader();
			instanceReader.setSource(new DefaultInputSupplier(
					getClass().getResource("/testdata/out/transformWrite_ERM_HPW.gml").toURI()));
			instanceReader.setSourceSchema(schema);

			instanceReader.validate();
			report = instanceReader.execute(null);
			assertTrue(report.isSuccess());

			FilterTest.complexinstances = instanceReader.getInstances();
			assertFalse(FilterTest.complexinstances.isEmpty());
			init = true;
		}
	}

	@Test
	public void simpleFilterTestCQL() throws CQLException {
		DefaultTypeDefinition stringType = new DefaultTypeDefinition(new QName("StringType"));
		stringType.setConstraint(Binding.get(String.class));

		DefaultTypeDefinition personDef = new DefaultTypeDefinition(new QName("PersonType"));
		personDef.addChild(new DefaultPropertyDefinition(new QName("Name"), personDef, stringType));

		DefaultTypeDefinition autoDef = new DefaultTypeDefinition(new QName("AutoType"));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Name"), autoDef, stringType));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Besitzer"), autoDef, personDef));

		MutableInstance auto = new DefaultInstance(autoDef, null);
		auto.addProperty(new QName("Name"), "Mein Porsche");
		MutableInstance ich = new DefaultInstance(personDef, null);
		ich.addProperty(new QName("Name"), "Ich");
		auto.addProperty(new QName("Besitzer"), ich);

		Filter filter;
		filter = new FilterGeoCqlImpl("Name = 'Mein Porsche'");
		assertTrue(filter.match(auto));
		Filter filter1 = new FilterGeoCqlImpl("Name like 'Porsche'");
		assertFalse(filter1.match(auto));
		Filter filter2 = new FilterGeoCqlImpl("Name like '%Porsche'");
		assertTrue(filter2.match(auto));
	}

	@Test
	public void simpleSchemaTestCQL() throws Exception {
		// Specify the path of the resource
		String resourcePath = "/testdata/GN_Point/GN_Point.zip";

		IOUtils.withTemporaryExtractedZipResource(resourcePath, FilterTest.class,
				(Path tempDirectory) -> {
					try {
						Path shpTempFile = tempDirectory.resolve("GN_Point.shp");

						InstanceCollection instances = validateSchemaAndInstanceReader(shpTempFile);

						performCQLFiltering(instances);
					} catch (IOProviderConfigurationException | IOException | CQLException e) {
						System.out.println(
								"One of " + e.getClass() + " is thrown because:" + e.getMessage());
					}
				});
	}

	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception if an error occurs
	 */
	@Ignore
	// not working due to changes in default behavior of XML reader (skip root
	// element)
	@Test
	public void testLoadShiporderCQL() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/testdata/shiporder/shiporder.xsd").toURI(),
				getClass().getResource("/testdata/shiporder/shiporder.xml").toURI());

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;
			boolean foundIt2 = false;

			Filter cqlfilter = new FilterGeoCqlImpl("shipto.city = '4000 Stavanger'");
			Filter foulfilter = new FilterGeoCqlImpl("HERP = 'DERP'");
			Filter foulfilter1 = new FilterGeoCqlImpl("shipto.city = 'HURR'");
			Filter cqlfilter2 = new FilterGeoCqlImpl(
					"\"{http://www.example.com}shipto.{http://www.example.com}city\" = '4000 Stavanger'");

			while (it.hasNext()) {
				Instance instance = it.next();
				assertNotNull(instance);

				if (cqlfilter.match(instance)) {
					foundIt = true;
				}
				if (foulfilter.match(instance)) {
					stayFalse = true;
				}
				if (foulfilter1.match(instance)) {
					stayFalseToo = true;
				}
				if (cqlfilter2.match(instance)) {
					foundIt2 = true;
				}
			}

			assertTrue(foundIt);
			assertTrue(foundIt2);
			assertFalse(stayFalse);
			assertFalse(stayFalseToo);
		} finally {
			it.close();
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void testComplexInstancesCQL() throws Exception {
		/*
		 * SchemaReader reader = new XmlSchemaReader();
		 * reader.setSharedTypes(null); reader.setSource(new
		 * DefaultInputSupplier
		 * ((getClass().getResource("/testdata/inspire3/HydroPhysicalWaters.xsd"
		 * ).toURI()))); IOReport report = reader.execute(null);
		 * assertTrue(report.isSuccess()); Schema schema = reader.getSchema();
		 * 
		 * StreamGmlReader instanceReader = new GmlInstanceReader();
		 * instanceReader.setSource(new
		 * DefaultInputSupplier(getClass().getResource
		 * ("/testdata/out/transformWrite_ERM_HPW.gml").toURI()));
		 * instanceReader.setSourceSchema(schema);
		 * 
		 * instanceReader.validate(); report = instanceReader.execute(null);
		 * assertTrue(report.isSuccess());
		 * 
		 * InstanceCollection instances = instanceReader.getInstances();
		 * assertFalse(instances.isEmpty());
		 */

		ResourceIterator<Instance> ri = FilterTest.complexinstances.iterator();
		try {
			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;
			boolean foundIt11 = false;
			boolean foundIt1 = false;
			boolean foundIt2 = false;
			boolean foundIt21 = false;
			boolean foundIt3 = false;
			boolean foundIt4 = false;
			boolean foundIt5 = false;

			Filter cqlfilter = new FilterGeoCqlImpl("\"geometry.Polygon.srsName\" = 'EPSG:4326'");
			Filter cqlfilter11 = new FilterGeoCqlImpl("width.WidthRange.upper = 15.0");
			Filter cqlfilter1 = new FilterGeoCqlImpl("\"width.WidthRange.upper\" = 15.0");
			Filter foulfilter1 = new FilterGeoCqlImpl("location.AbstractSolid.id = 'HURR'");
			Filter foulfilter = new FilterGeoCqlImpl("HERP = 'DERP'");
			Filter cqlfilter2 = new FilterGeoCqlImpl(
					"id = '_00000000-7953-b57f-0000-00000010cb14'");
			Filter cqlfilter21 = new FilterGeoCqlImpl(
					"\"id\" = '_00000000-7953-b57f-0000-00000010cb14'");

			Filter cqlfilter3 = new FilterGeoCqlImpl("\"geometry.Polygon.srsName\" = 'EPSG:4326'");
//			Filter cqlfilter4 = new FilterGeoCqlImpl(
//					"\"geometry.{http://www.opengis.net/gml/3.2}Polygon.srsName\" = 'EPSG:4326'");
//			Filter cqlfilter5 = new FilterGeoCqlImpl(
//					"\"{http://www.opengis.net/gml/3.2}geometry.Polygon.srsName\" = 'EPSG:4326'");

			while (ri.hasNext()) {
				Instance inst = ri.next();
				assertNotNull(inst);

				if (cqlfilter.match(inst)) {
					foundIt = true;
				}
				if (cqlfilter1.match(inst)) {
					foundIt1 = true;
				}
				if (cqlfilter11.match(inst)) {
					foundIt11 = true;
				}
				if (foulfilter.match(inst)) {
					stayFalse = true;
				}
				if (foulfilter1.match(inst)) {
					stayFalseToo = true;
				}
				if (cqlfilter2.match(inst)) {
					foundIt2 = true;
				}
				if (cqlfilter21.match(inst)) {
					foundIt21 = true;
				}
				if (cqlfilter3.match(inst)) {
					foundIt3 = true;
				}
				/*
				 * if(cqlfilter4.match(inst)){ foundIt4 = true; }
				 * if(cqlfilter5.match(inst)){ foundIt5 = true; }
				 */
			}

			assertTrue(foundIt);
			assertTrue(foundIt1);
			assertTrue(foundIt11);
			assertTrue(foundIt2);
			assertTrue(foundIt21);
			assertTrue(foundIt3);
			// assertTrue(foundIt4);
			// assertTrue(foundIt5);
			assertFalse(stayFalse);
			assertFalse(stayFalseToo);

			// TODO
		} finally {
			ri.close();
		}
	}

	@Test
	public void simpleFilterTestECQL() throws CQLException {
		DefaultTypeDefinition stringType = new DefaultTypeDefinition(new QName("StringType"));
		stringType.setConstraint(Binding.get(String.class));

		DefaultTypeDefinition personDef = new DefaultTypeDefinition(new QName("PersonType"));
		personDef.addChild(new DefaultPropertyDefinition(new QName("Name"), personDef, stringType));

		DefaultTypeDefinition autoDef = new DefaultTypeDefinition(new QName("AutoType"));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Name"), autoDef, stringType));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Besitzer"), autoDef, personDef));

		MutableInstance auto = new DefaultInstance(autoDef, null);
		auto.addProperty(new QName("Name"), "Mein Porsche");
		MutableInstance ich = new DefaultInstance(personDef, null);
		ich.addProperty(new QName("Name"), "Ich");
		auto.addProperty(new QName("Besitzer"), ich);

		Filter filter;
		filter = new FilterGeoECqlImpl("Name = 'Mein Porsche'");
		assertTrue(filter.match(auto));
		Filter filter1 = new FilterGeoECqlImpl("Name like '%Porsche%'");
		assertTrue(filter1.match(auto));
	}

	@Test
	public void simpleSchemaTestECQL() throws Exception {
		// Specify the path of the resource
		String resourcePath = "/testdata/GN_Point/GN_Point.zip";

		IOUtils.withTemporaryExtractedZipResource(resourcePath, FilterTest.class,
				(Path tempDirectory) -> {
					try {
						Path shpTempFile = tempDirectory.resolve("GN_Point.shp");

						InstanceCollection instances = validateSchemaAndInstanceReader(shpTempFile);

						performECQLFiltering(instances);
					} catch (IOProviderConfigurationException | IOException | CQLException e) {
						System.out.println(
								"One of " + e.getClass() + " is thrown because:" + e.getMessage());
					}
				});
	}

	private void performFiltering(InstanceCollection instances, Filter cqlFilter, Filter foulFilter,
			Filter foulFilter1) {
		try (ResourceIterator<Instance> ri = instances.iterator()) {
			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;

			while (ri.hasNext()) {
				Instance inst = ri.next();
				assertNotNull(inst);

				if (cqlFilter.match(inst)) {
					foundIt = true;
				}
				if (foulFilter.match(inst)) {
					stayFalse = true;
				}
				if (foulFilter1.match(inst)) {
					stayFalseToo = true;
				}
			}

			assertTrue(foundIt);
			assertFalse(stayFalse);
			assertFalse(stayFalseToo);
		}
	}

	private void performECQLFiltering(InstanceCollection instances) throws CQLException {
		Filter cqlFilter = new FilterGeoECqlImpl("NEV = 'Piritulus'");
		Filter foulFilter = new FilterGeoECqlImpl("HERP = 'DERP'");
		Filter foulFilter1 = new FilterGeoECqlImpl("NEV = 'HURR'");
		performFiltering(instances, cqlFilter, foulFilter, foulFilter1);
	}

	private void performCQLFiltering(InstanceCollection instances) throws CQLException {
		Filter cqlFilter = new FilterGeoCqlImpl("NEV = 'Piritulus'");
		Filter foulFilter = new FilterGeoCqlImpl("HERP = 'DERP'");
		Filter foulFilter1 = new FilterGeoCqlImpl("NEV = 'HURR'");
		performFiltering(instances, cqlFilter, foulFilter, foulFilter1);
	}

	/**
	 * @param shpTempFile
	 * @return
	 * @throws IOProviderConfigurationException
	 * @throws IOException
	 */
	private InstanceCollection validateSchemaAndInstanceReader(Path shpTempFile)
			throws IOProviderConfigurationException, IOException {
		ShapeSchemaReader schemaReader = new ShapeSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(shpTempFile.toUri()));
		schemaReader.validate();

		IOReport report = schemaReader.execute(null);
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();

		ShapeInstanceReader instanceReader = new ShapeInstanceReader();
		instanceReader.setSource(new DefaultInputSupplier(shpTempFile.toUri()));
		instanceReader.setSourceSchema(schema);

		instanceReader.validate();
		report = instanceReader.execute(null);
		assertTrue(report.isSuccess());

		InstanceCollection instances = instanceReader.getInstances();
		assertFalse(instances.isEmpty());
		return instances;
	}

	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception if an error occurs
	 */
	@Ignore
	// not working due to changes in default behavior of XML reader (skip root
	// element)
	@Test
	public void testLoadShiporderECQL() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/testdata/shiporder/shiporder.xsd").toURI(),
				getClass().getResource("/testdata/shiporder/shiporder.xml").toURI());

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;

			// von CQL nicht unterstützt
			// Filter cqlfilter = new
			// FilterGeoCqlImpl("{http://www.example.com}shipto.{http://www.example.com}city
			// = '4000 Stavanger'");

			Filter cqlfilter = new FilterGeoECqlImpl("shipto.city = '4000 Stavanger'");
			Filter foulfilter = new FilterGeoECqlImpl("HERP = 'DERP'");
			Filter foulfilter1 = new FilterGeoECqlImpl("shipto.city = 'HURR'");

			while (it.hasNext()) {
				Instance instance = it.next();
				assertNotNull(instance);

				if (cqlfilter.match(instance)) {
					foundIt = true;
				}
				if (foulfilter.match(instance)) {
					stayFalse = true;
				}
				if (foulfilter1.match(instance)) {
					stayFalseToo = true;
				}
			}

			assertTrue(foundIt);
			assertFalse(stayFalse);
			assertFalse(stayFalseToo);
		} finally {
			it.close();
		}
	}

	@Test
	public void testComplexInstancesECQL() throws Exception {
		/*
		 * SchemaReader reader = new XmlSchemaReader();
		 * reader.setSharedTypes(null); reader.setSource(new
		 * DefaultInputSupplier
		 * ((getClass().getResource("/testdata/inspire3/HydroPhysicalWaters.xsd"
		 * ).toURI()))); IOReport report = reader.execute(null);
		 * assertTrue(report.isSuccess()); Schema schema = reader.getSchema();
		 * 
		 * StreamGmlReader instanceReader = new GmlInstanceReader();
		 * instanceReader.setSource(new
		 * DefaultInputSupplier(getClass().getResource
		 * ("/testdata/out/transformWrite_ERM_HPW.gml").toURI()));
		 * instanceReader.setSourceSchema(schema);
		 * 
		 * instanceReader.validate(); report = instanceReader.execute(null);
		 * assertTrue(report.isSuccess());
		 * 
		 * InstanceCollection instances = instanceReader.getInstances();
		 * assertFalse(instances.isEmpty());
		 */

		ResourceIterator<Instance> ri = FilterTest.complexinstances.iterator();
		try {
			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;
			boolean foundIt11 = false;
			boolean foundIt1 = false;
			boolean foundIt2 = false;
			boolean foundIt3 = false;

			Filter ecqlfilter = new FilterGeoECqlImpl("\"geometry.Polygon.srsName\" = 'EPSG:4326'");
			Filter ecqlfilter11 = new FilterGeoECqlImpl("width.WidthRange.upper = 15.0");
			Filter ecqlfilter1 = new FilterGeoECqlImpl("\"width.WidthRange.upper\" = 15.0");
			Filter foulfilter1 = new FilterGeoECqlImpl("\"location.AbstractSolid.id\" = 'HURR'");
			Filter foulfilter = new FilterGeoECqlImpl("HERP = 'DERP'");
			Filter ecqlfilter2 = new FilterGeoECqlImpl(
					"\"id\" = '_00000000-7953-b57f-0000-00000010cb14'");
			Filter ecqlfilter3 = new FilterGeoECqlImpl(
					"'_00000000-7953-b57f-0000-00000010cb14' = \"id\"");

			// this should throw a CQL Exception
			try {
				new FilterGeoECqlImpl("id = '_00000000-7953-b57f-0000-00000010cb14'");

				fail("Expected exception!");
			} catch (CQLException e) {
				System.out.println("CQL Exception thrown because \"id\" is reserved");
			}

			while (ri.hasNext()) {
				Instance inst = ri.next();
				assertNotNull(inst);

				if (ecqlfilter.match(inst)) {
					foundIt = true;
				}
				if (ecqlfilter1.match(inst)) {
					foundIt1 = true;
				}
				if (ecqlfilter11.match(inst)) {
					foundIt11 = true;
				}
				if (foulfilter.match(inst)) {
					stayFalse = true;
				}
				if (foulfilter1.match(inst)) {
					stayFalseToo = true;
				}
				if (ecqlfilter2.match(inst)) {
					foundIt2 = true;
				}
				if (ecqlfilter3.match(inst)) {
					foundIt3 = true;
				}
			}

			assertTrue(foundIt);
			assertTrue(foundIt1);
			assertTrue(foundIt11);
			assertTrue(foundIt2);
			assertTrue(foundIt3);
			assertFalse(stayFalse);
			assertFalse(stayFalseToo);

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

		instanceReader.setSource(new DefaultInputSupplier(xmlLocation));
		instanceReader.setSourceSchema(sourceSchema);

		IOReport instanceReport = instanceReader.execute(null);
		assertTrue(instanceReport.isSuccess());

		return instanceReader.getInstances();
	}

}
