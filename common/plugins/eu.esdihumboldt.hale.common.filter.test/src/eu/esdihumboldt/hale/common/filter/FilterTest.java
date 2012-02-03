/*
f * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.geotools.filter.text.cql2.CQLException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
import de.fhg.igd.osgi.util.OsgiUtils.Condition;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceReader;
import eu.esdihumboldt.hale.io.gml.reader.internal.StreamGmlReader;
import eu.esdihumboldt.hale.io.gml.reader.internal.XmlInstanceReader;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeInstanceReader;
import eu.esdihumboldt.hale.io.shp.reader.internal.ShapeSchemaReader;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * TODO Type description
 * 
 * @author Basti
 */
@SuppressWarnings("restriction")
public class FilterTest {

	static InstanceCollection complexinstances;
	static boolean init = false;

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		assertTrue("Conversion service not available",
				OsgiUtils.waitUntil(new Condition() {
					@Override
					public boolean evaluate() {
						return OsgiUtils.getService(ConversionService.class) != null;
					}
				}, 30));
	}

	@Before
	public void loadXML() throws Exception {
		if (init == false) {
			SchemaReader reader = new XmlSchemaReader();
			reader.setSharedTypes(null);
			reader.setSource(new DefaultInputSupplier((getClass().getResource(
					"/testdata/inspire3/HydroPhysicalWaters.xsd").toURI())));
			IOReport report = reader.execute(null);
			assertTrue(report.isSuccess());
			Schema schema = reader.getSchema();

			StreamGmlReader instanceReader = new GmlInstanceReader();
			instanceReader.setSource(new DefaultInputSupplier(getClass()
					.getResource("/testdata/out/transformWrite_ERM_HPW.gml")
					.toURI()));
			instanceReader.setSourceSchema(schema);

			instanceReader.validate();
			report = instanceReader.execute(null);
			assertTrue(report.isSuccess());

			this.complexinstances = instanceReader.getInstances();
			assertFalse(this.complexinstances.isEmpty());
			init = true;
		}
	}

	@Test
	public void simpleFilterTestCQL() {
		DefaultTypeDefinition stringType = new DefaultTypeDefinition(new QName(
				"StringType"));
		stringType.setConstraint(Binding.get(String.class));

		DefaultTypeDefinition personDef = new DefaultTypeDefinition(new QName(
				"PersonType"));
		personDef.addChild(new DefaultPropertyDefinition(new QName("Name"),
				personDef, stringType));

		DefaultTypeDefinition autoDef = new DefaultTypeDefinition(new QName(
				"AutoType"));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Name"),
				autoDef, stringType));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Besitzer"),
				autoDef, personDef));

		MutableInstance auto = new OInstance(autoDef, null);
		auto.addProperty(new QName("Name"), "Mein Porsche");
		MutableInstance ich = new OInstance(personDef, null);
		ich.addProperty(new QName("Name"), "Ich");
		auto.addProperty(new QName("Besitzer"), ich);

		Filter filter;
		try {
			filter = new FilterGeoCqlImpl("Name = 'Mein Porsche'");
			assertTrue(filter.match(auto));
			Filter filter1 = new FilterGeoCqlImpl("Name like 'Porsche'");
			assertTrue(filter.match(auto));
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void simpleSchemaTestCQL() throws Exception {
		ShapeSchemaReader schemaReader = new ShapeSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(getClass().getResource(
				"/testdata/Gn_Point/GN_Point.shp").toURI()));

		schemaReader.validate();
		IOReport report = schemaReader.execute(null);
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();

		ShapeInstanceReader instanceReader = new ShapeInstanceReader();
		instanceReader.setSource(new DefaultInputSupplier(getClass()
				.getResource("/testdata/Gn_Point/GN_Point.shp").toURI()));
		instanceReader.setSourceSchema(schema);

		instanceReader.validate();
		report = instanceReader.execute(null);
		assertTrue(report.isSuccess());

		InstanceCollection instances = instanceReader.getInstances();
		assertFalse(instances.isEmpty());

		ResourceIterator<Instance> ri = instances.iterator();
		try {

			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;

			Filter cqlfilter = new FilterGeoCqlImpl("NEV = 'Piritulus'");
			Filter foulfilter = new FilterGeoCqlImpl("HERP = 'DERP'");
			Filter foulfilter1 = new FilterGeoCqlImpl("NEV = 'HURR'");

			while (ri.hasNext()) {
				Instance inst = ri.next();
				assertNotNull(inst);

				if (cqlfilter.match(inst)) {
					foundIt = true;
				}
				if (foulfilter.match(inst)) {
					stayFalse = true;
				}
				if (foulfilter1.match(inst)) {
					stayFalseToo = true;
				}
			}

			assertTrue(foundIt);
			assertFalse(stayFalse);
			assertFalse(stayFalseToo);
		} finally {
			ri.close();
		}

	}

	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testLoadShiporderCQL() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/testdata/shiporder/shiporder.xsd")
						.toURI(),
				getClass().getResource("/testdata/shiporder/shiporder.xml")
						.toURI());

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());
	
			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;
			boolean foundIt2 = false;
	
			Filter cqlfilter = new FilterGeoCqlImpl(
					"shipto.city = '4000 Stavanger'");
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

		ResourceIterator<Instance> ri = this.complexinstances.iterator();
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
	
			Filter cqlfilter = new FilterGeoCqlImpl(
					"\"geometry.Polygon.srsName\" = 'EPSG:4326'");
			Filter cqlfilter11 = new FilterGeoCqlImpl(
					"width.WidthRange.upper = 15.0");
			Filter cqlfilter1 = new FilterGeoCqlImpl(
					"\"width.WidthRange.upper\" = 15.0");
			Filter foulfilter1 = new FilterGeoCqlImpl(
					"location.AbstractSolid.id = 'HURR'");
			Filter foulfilter = new FilterGeoCqlImpl("HERP = 'DERP'");
			Filter cqlfilter2 = new FilterGeoCqlImpl(
					"id = '_00000000-7953-b57f-0000-00000010cb14'");
			Filter cqlfilter21 = new FilterGeoCqlImpl(
					"\"id\" = '_00000000-7953-b57f-0000-00000010cb14'");
	
			Filter cqlfilter3 = new FilterGeoCqlImpl(
					"\"geometry.Polygon.srsName\" = 'EPSG:4326'");
			Filter cqlfilter4 = new FilterGeoCqlImpl(
					"\"geometry.{http://www.opengis.net/gml/3.2}Polygon.srsName\" = 'EPSG:4326'");
			Filter cqlfilter5 = new FilterGeoCqlImpl(
					"\"{http://www.opengis.net/gml/3.2}geometry.Polygon.srsName\" = 'EPSG:4326'");
	
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
	public void simpleFilterTestECQL() {
		DefaultTypeDefinition stringType = new DefaultTypeDefinition(new QName(
				"StringType"));
		stringType.setConstraint(Binding.get(String.class));

		DefaultTypeDefinition personDef = new DefaultTypeDefinition(new QName(
				"PersonType"));
		personDef.addChild(new DefaultPropertyDefinition(new QName("Name"),
				personDef, stringType));

		DefaultTypeDefinition autoDef = new DefaultTypeDefinition(new QName(
				"AutoType"));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Name"),
				autoDef, stringType));
		autoDef.addChild(new DefaultPropertyDefinition(new QName("Besitzer"),
				autoDef, personDef));

		MutableInstance auto = new OInstance(autoDef, null);
		auto.addProperty(new QName("Name"), "Mein Porsche");
		MutableInstance ich = new OInstance(personDef, null);
		ich.addProperty(new QName("Name"), "Ich");
		auto.addProperty(new QName("Besitzer"), ich);

		Filter filter;
		try {
			filter = new FilterGeoECqlImpl("Name = 'Mein Porsche'");
			assertTrue(filter.match(auto));
			Filter filter1 = new FilterGeoECqlImpl("Name like %Porsche%");
			assertTrue(filter.match(auto));
		} catch (CQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void simpleSchemaTestECQL() throws Exception {
		ShapeSchemaReader schemaReader = new ShapeSchemaReader();
		schemaReader.setSource(new DefaultInputSupplier(getClass().getResource(
				"/testdata/Gn_Point/GN_Point.shp").toURI()));

		schemaReader.validate();
		IOReport report = schemaReader.execute(null);
		assertTrue(report.isSuccess());

		Schema schema = schemaReader.getSchema();

		ShapeInstanceReader instanceReader = new ShapeInstanceReader();
		instanceReader.setSource(new DefaultInputSupplier(getClass()
				.getResource("/testdata/Gn_Point/GN_Point.shp").toURI()));
		instanceReader.setSourceSchema(schema);

		instanceReader.validate();
		report = instanceReader.execute(null);
		assertTrue(report.isSuccess());

		InstanceCollection instances = instanceReader.getInstances();
		assertFalse(instances.isEmpty());

		ResourceIterator<Instance> ri = instances.iterator();
		try {
			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;
	
			Filter cqlfilter = new FilterGeoECqlImpl("NEV = 'Piritulus'");
			Filter foulfilter = new FilterGeoECqlImpl("HERP = 'DERP'");
			Filter foulfilter1 = new FilterGeoECqlImpl("NEV = 'HURR'");
	
			while (ri.hasNext()) {
				Instance inst = ri.next();
				assertNotNull(inst);
	
				if (cqlfilter.match(inst)) {
					foundIt = true;
				}
				if (foulfilter.match(inst)) {
					stayFalse = true;
				}
				if (foulfilter1.match(inst)) {
					stayFalseToo = true;
				}
			}
	
			assertTrue(foundIt);
			assertFalse(stayFalse);
			assertFalse(stayFalseToo);
		} finally {
			ri.close();
		}

	}

	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testLoadShiporderECQL() throws Exception {
		InstanceCollection instances = loadXMLInstances(
				getClass().getResource("/testdata/shiporder/shiporder.xsd")
						.toURI(),
				getClass().getResource("/testdata/shiporder/shiporder.xml")
						.toURI());

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());
	
			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;
	
			// von CQL nicht unterstützt
			// Filter cqlfilter = new
			// FilterGeoCqlImpl("{http://www.example.com}shipto.{http://www.example.com}city = '4000 Stavanger'");
	
			Filter cqlfilter = new FilterGeoECqlImpl(
					"shipto.city = '4000 Stavanger'");
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

		ResourceIterator<Instance> ri = this.complexinstances.iterator();
		try {
			boolean foundIt = false;
			boolean stayFalse = false;
			boolean stayFalseToo = false;
			boolean foundIt11 = false;
			boolean foundIt1 = false;
			boolean foundIt2 = false;
			boolean foundIt3 = false;
	
			Filter ecqlfilter = new FilterGeoECqlImpl(
					"\"geometry.Polygon.srsName\" = 'EPSG:4326'");
			Filter ecqlfilter11 = new FilterGeoECqlImpl(
					"width.WidthRange.upper = 15.0");
			Filter ecqlfilter1 = new FilterGeoECqlImpl(
					"\"width.WidthRange.upper\" = 15.0");
			Filter foulfilter1 = new FilterGeoECqlImpl(
					"\"location.AbstractSolid.id\" = 'HURR'");
			Filter foulfilter = new FilterGeoECqlImpl("HERP = 'DERP'");
			Filter ecqlfilter2 = new FilterGeoECqlImpl(
					"\"id\" = '_00000000-7953-b57f-0000-00000010cb14'");
			Filter ecqlfilter3 = new FilterGeoECqlImpl(
					"'_00000000-7953-b57f-0000-00000010cb14' = \"id\"");
	
			// this should throw a CQL Exception
			try {
				new FilterGeoECqlImpl(
						"id = '_00000000-7953-b57f-0000-00000010cb14'");
	
				fail("Expected exception!");
			} catch (CQLException e) {
				System.out
						.println("CQL Exception thrown because \"id\" is reserved");
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

	private InstanceCollection loadXMLInstances(URI schemaLocation,
			URI xmlLocation) throws IOException,
			IOProviderConfigurationException {
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