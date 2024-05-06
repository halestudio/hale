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

package eu.esdihumboldt.hale.io.gml.reader.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.test.TestUtil;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Tests for {@link GmlInstanceCollection}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("restriction")
public class GmlInstanceCollectionTest {

	/**
	 * Wait for needed services to be running
	 */
	@BeforeClass
	public static void waitForServices() {
		TestUtil.startConversionService();
	}

	/**
	 * Test loading a simple XML file with one instance, containing mixed
	 * content elements.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadMixed() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/mixed/shiporder.xsd").toURI(),
				getClass().getResource("/data/mixed/shiporder.xml").toURI(), false);

		String ns = "http://www.example.com";

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			// items
			Object[] items = instance.getProperty(new QName(ns, "item"));
			assertNotNull(items);
			assertEquals(3, items.length);

			// item 1
			Object item1 = items[0];
			assertTrue(item1 instanceof Instance);

			/*
			 * XXX mixed content properties currently are treated rather
			 * special, currently ignoring elements and only using the
			 * attributes and text.
			 */

			Object[] note1 = ((Instance) item1).getProperty(new QName(ns, "note"));
			assertNotNull(note1);
			assertEquals(1, note1.length);
			// expected to be an instance
//			assertTrue("Mixed content expected to be an Instance", note1[0] instanceof Instance);
//			Instance note1Inst = (Instance) note1[0];
//			assertEquals("Special Edition", note1Inst.getValue());
			// expected to be a value
			assertEquals("Special Edition", note1[0]);

			// item 2
			Object item2 = items[1];
			assertTrue(item2 instanceof Instance);

			Object[] note2 = ((Instance) item2).getProperty(new QName(ns, "note"));
			assertNotNull(note2);
			assertEquals(1, note2.length);
			// expected to be an instance
//			assertTrue("Mixed content expected to be an Instance", note2[0] instanceof Instance);
//			Instance note2Inst = (Instance) note2[0];
//			assertEquals("Save 10%", note2Inst.getValue());
			// expected to be a value
			assertEquals("Save 10%", note2[0]);

			// item 3
			Object item3 = items[2];
			assertTrue(item3 instanceof Instance);

			Object[] note3 = ((Instance) item3).getProperty(new QName(ns, "note"));
			assertNotNull(note3);
			assertEquals(1, note3.length);
			// expected to be an instance
//			assertTrue("Mixed content expected to be an Instance", note3[0] instanceof Instance);
//			Instance note3Inst = (Instance) note3[0];
//			assertEquals("Nearly sold out", note3Inst.getValue());
			// expected to be a value
			assertEquals("Nearly sold out", note3[0]);
		} finally {
			it.close();
		}
	}

	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadShiporder() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/shiporder/shiporder.xsd").toURI(),
				getClass().getResource("/data/shiporder/shiporder.xml").toURI(), false);

		String ns = "http://www.example.com";

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			Object[] orderid = instance.getProperty(new QName("orderid"));
			// attribute
			// form
			// not
			// qualified

			assertNotNull(orderid);
			assertEquals(1, orderid.length);
			assertEquals("889923", orderid[0]);

			Object[] orderperson = instance.getProperty(new QName(ns, "orderperson"));
			assertNotNull(orderperson);
			assertEquals(1, orderperson.length);
			assertEquals("John Smith", orderperson[0]);

			Object[] shipto = instance.getProperty(new QName(ns, "shipto"));
			assertNotNull(shipto);
			assertEquals(1, shipto.length);
			assertTrue(shipto[0] instanceof Instance);
			Instance shipto1 = (Instance) shipto[0];

			Object[] shiptoName = shipto1.getProperty(new QName(ns, "name"));
			assertNotNull(shiptoName);
			assertEquals(1, shiptoName.length);
			assertEquals("Ola Nordmann", shiptoName[0]);

			Object[] shiptoAddress = shipto1.getProperty(new QName(ns, "address"));
			assertNotNull(shiptoAddress);
			assertEquals(1, shiptoAddress.length);
			assertEquals("Langgt 23", shiptoAddress[0]);

			Object[] shiptoCity = shipto1.getProperty(new QName(ns, "city"));
			assertNotNull(shiptoCity);
			assertEquals(1, shiptoCity.length);
			assertEquals("4000 Stavanger", shiptoCity[0]);

			Object[] shiptoCountry = shipto1.getProperty(new QName(ns, "country"));
			assertNotNull(shiptoCountry);
			assertEquals(1, shiptoCountry.length);
			assertEquals("Norway", shiptoCountry[0]);

			// items
			Object[] items = instance.getProperty(new QName(ns, "item"));
			assertNotNull(items);
			assertEquals(2, items.length);

			// item 1
			Object item1 = items[0];
			assertTrue(item1 instanceof Instance);

			Object[] title1 = ((Instance) item1).getProperty(new QName(ns, "title"));
			assertNotNull(title1);
			assertEquals(1, title1.length);
			assertEquals("Empire Burlesque", title1[0]);

			Object[] price1 = ((Instance) item1).getProperty(new QName(ns, "price"));
			assertNotNull(price1);
			BigDecimal bigDecimal = new BigDecimal("0.00000000000");
			assertEquals(bigDecimal, price1[0]);

			// item 2
			Object item2 = items[1];
			assertTrue(item2 instanceof Instance);

			Object[] title2 = ((Instance) item2).getProperty(new QName(ns, "title"));
			assertNotNull(title2);
			assertEquals(1, title2.length);
			assertEquals("Hide your heart", title2[0]);

			Object[] price2 = ((Instance) item2).getProperty(new QName(ns, "price"));
			assertNotNull(price2);
			bigDecimal = new BigDecimal("9.90");
			assertEquals(bigDecimal, price2[0]);

			Object[] priceInteger = ((Instance) item2).getProperty(new QName(ns, "priceInteger"));
			int intNumber = 123;
			assertEquals(intNumber, priceInteger[0]);

			// Double
			Object[] priceDouble = ((Instance) item2).getProperty(new QName(ns, "priceDouble"));
			double doubleNumber = 12.3;
			assertEquals(doubleNumber, priceDouble[0]);

			// Float
			Object[] priceFloat = ((Instance) item2).getProperty(new QName(ns, "priceFloat"));
			float floatNumber = 123.456F;
			assertEquals(floatNumber, priceFloat[0]);

			// Long
			Object[] priceLong = ((Instance) item2).getProperty(new QName(ns, "priceLong"));
			long longNumber = 1234567890123456789L;
			assertEquals(longNumber, priceLong[0]);

			// short
			Object[] priceShort = ((Instance) item2).getProperty(new QName(ns, "priceShort"));
			short shortNumber = 12345;
			assertEquals(shortNumber, priceShort[0]);

			// only one object
			assertFalse(it.hasNext());
		} finally {
			it.close();
		}
	}

	/**
	 * Test loading a simple XML file with one instance including a choice.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadChoice() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/group/choice.xsd").toURI(),
				getClass().getResource("/data/group/choice.xml").toURI(), false);

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			// choice
			Object[] choice_1 = instance.getProperty(new QName("/ItemsType", "choice_1"));
			assertNotNull(choice_1);
			assertEquals(5, choice_1.length);

			String[] expectedProperties = new String[] { "shirt", "hat", "shirt", "umbrella",
					"hat" };
			for (int i = 0; i < choice_1.length; i++) {
				assertTrue(choice_1[i] instanceof Group);
				Group choice = (Group) choice_1[i];
				String expectedProperty = expectedProperties[i];

				int num = 0;
				for (QName name : choice.getPropertyNames()) {
					assertEquals(0, num++); // expecting only one property
					assertEquals(new QName(expectedProperty), name);
				}
			}

			// only one object
			assertFalse(it.hasNext());
		} finally {
			it.close();
		}
	}

	/**
	 * Test loading a simple XML file with one instance including an <xs:all>
	 * group.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadAllGroup() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/allgroup/allgroup.xsd").toURI(),
				getClass().getResource("/data/allgroup/allgroup.xml").toURI(), false, false, false);

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			Object[] umbrella = instance.getProperty(new QName("umbrella"));
			assertNotNull(umbrella);
			assertEquals(1, umbrella.length);
			assertEquals("Some umbrella", umbrella[0]);

			Object[] shirt = instance.getProperty(new QName("shirt"));
			assertNotNull(shirt);
			assertEquals(1, shirt.length);
			assertEquals("Some shirt", shirt[0]);

			Object[] hat = instance.getProperty(new QName("hat"));
			assertNotNull(hat);
			assertEquals(1, hat.length);
			assertEquals("And hat", hat[0]);

			// only one object
			assertFalse(it.hasNext());
		} finally {
			it.close();
		}
	}

	/**
	 * Test loading a simple XML file with one instance including a choice and a
	 * sub-choice.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadChoice2() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/group/choice2.xsd").toURI(),
				getClass().getResource("/data/group/choice2.xml").toURI(), false);

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			// choice
			Object[] choice_1 = instance.getProperty(new QName("/ItemsType", "choice_1"));
			assertNotNull(choice_1);
			assertEquals(7, choice_1.length);

			// TODO

			// only one object
			assertFalse(it.hasNext());
		} finally {
			it.close();
		}
	}

	/**
	 * Test loading a (relatively) simple GML file with one instance. Includes
	 * groups and a geometry.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadWVA() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/sample_wva/wfs_va.xsd").toURI(),
				getClass().getResource("/data/sample_wva/wfs_va_sample.gml").toURI(), true);

		testWVAInstances(instances);
	}

	/**
	 * Test loading a (relatively) simple GML file with one instance. Includes
	 * groups and a geometry.
	 * 
	 * The namespace in the file differs from the schema namespace. The
	 * ignoreNamespace setting is used to load the file nethertheless.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadWVAIgnoreNamespace() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/sample_wva/wfs_va.xsd").toURI(),
				getClass().getResource("/data/sample_wva/wfs_va_sample_namespace.gml").toURI(),
				true, true, true);

		testWVAInstances(instances);
	}

	private void testWVAInstances(InstanceCollection instances) {
		String ns = "http://www.esdi-humboldt.org/waterVA";
		String gmlNs = "http://www.opengis.net/gml";

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			// check type and element

			TypeDefinition type = instance.getDefinition();
			assertEquals(new QName(ns, "Watercourses_VA_Type"), type.getName());
			XmlElements elements = type.getConstraint(XmlElements.class);
			Collection<? extends XmlElement> elementCollection = elements.getElements();
			assertEquals(1, elementCollection.size());
			XmlElement element = elementCollection.iterator().next();
			assertEquals(new QName(ns, "Watercourses_VA"), element.getName());

			// check instance

			// check a simple property first (FGW_ID)
			Object[] fgwID = instance.getProperty(new QName(ns, "FGW_ID"));
			assertNotNull(fgwID);
			assertEquals(1, fgwID.length);
			assertEquals("81011403", fgwID[0]);

			// the_geom
			Object[] the_geom = instance.getProperty(new QName(ns, "the_geom"));
			assertNotNull(the_geom);
			assertEquals(1, the_geom.length);
			assertTrue(the_geom[0] instanceof Instance);

			// MultiLineString
			Object[] multiLineString = ((Instance) the_geom[0])
					.getProperty(new QName(gmlNs, "MultiLineString"));
			assertNotNull(multiLineString);
			assertEquals(1, multiLineString.length);
			assertTrue(multiLineString[0] instanceof Instance);

			// TODO the MultiLineString should have a GeometryProperty value
			// with a MultiLineString as geometry and a CRS definition
			// ...getValue()

			// srsName
			Object[] srsName = ((Instance) multiLineString[0]).getProperty(new QName("srsName"));
			assertNotNull(srsName);
			assertEquals(1, srsName.length);
			assertEquals("EPSG:31251", srsName[0].toString());

			// lineStringMember
			Object[] lineStringMember = ((Instance) multiLineString[0])
					.getProperty(new QName(gmlNs, "lineStringMember"));
			assertNotNull(lineStringMember);
			assertEquals(1, lineStringMember.length);
			assertTrue(lineStringMember[0] instanceof Instance);

			// LineString
			Object[] lineString = ((Instance) lineStringMember[0])
					.getProperty(new QName(gmlNs, "LineString"));
			assertNotNull(lineString);
			assertEquals(1, lineString.length);
			assertTrue(lineString[0] instanceof Instance);

			// TODO the LineString should have a GeometryProperty value with a
			// LineString as geometry and a CRS definition
			// ...getValue()

			// choice
			Object[] choice_1 = ((Instance) lineString[0])
					.getProperty(new QName(gmlNs + "/LineStringType", "choice_1"));
			assertNotNull(choice_1);
			assertEquals(1, choice_1.length);
			assertTrue(choice_1[0] instanceof Group);

			// coordinates
			Object[] coordinates = ((Group) choice_1[0])
					.getProperty(new QName(gmlNs, "coordinates"));
			assertNotNull(coordinates);
			assertEquals(1, coordinates.length);
			assertTrue(coordinates[0] instanceof Instance);
			assertTrue(
					((Instance) coordinates[0]).getValue().toString().contains("-39799.68820381"));

			// only one instance should be present
			assertFalse(it.hasNext());
		} finally {
			it.close();
		}
	}

	/**
	 * Test loading a (relatively) simple GML file with one instance. Includes
	 * groups and a geometry.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadImgeo_Scheiding() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/sample_imgeo/IMGEO.xsd").toURI(),
				getClass().getResource("/data/sample_imgeo/sample_scheiding_nofc.gml").toURI(),
				true);

//		String ns = "http://www.geonovum.nl/IMGEO";
//		String gmlNs = "http://www.opengis.net/gml";

		ResourceIterator<Instance> it = instances.iterator();
		try {
			assertTrue(it.hasNext());

			Instance instance = it.next();
			assertNotNull(instance);

			// TODO

			// only one instance should be present
			assertFalse(it.hasNext());
		} finally {
			it.close();
		}
	}

	private GmlInstanceCollection loadInstances(URI schemaLocation, URI xmlLocation,
			boolean restrictToFeatures) throws IOException, IOProviderConfigurationException {
		return loadInstances(schemaLocation, xmlLocation, restrictToFeatures, true, true);
	}

	private GmlInstanceCollection loadInstances(URI schemaLocation, URI xmlLocation,
			boolean restrictToFeatures, boolean ignoreNamespace, boolean strict)
			throws IOException, IOProviderConfigurationException {
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(schemaLocation));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		Schema sourceSchema = reader.getSchema();

		return new GmlInstanceCollection(new DefaultInputSupplier(xmlLocation), sourceSchema,
				restrictToFeatures, false, strict, ignoreNamespace, null, reader);
	}

}
