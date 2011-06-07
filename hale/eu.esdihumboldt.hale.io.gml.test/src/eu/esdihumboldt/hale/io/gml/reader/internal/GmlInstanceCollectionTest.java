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

package eu.esdihumboldt.hale.io.gml.reader.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.junit.Test;

import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;
import eu.esdihumboldt.hale.schema.io.SchemaReader;
import eu.esdihumboldt.hale.schema.model.Schema;


/**
 * Tests for {@link GmlInstanceCollection}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
@SuppressWarnings("restriction")
public class GmlInstanceCollectionTest {
	
	/**
	 * Test loading a simple XML file with one instance
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoadShiporder() throws Exception {
		GmlInstanceCollection instances = loadInstances(
				getClass().getResource("/data/shiporder/shiporder.xsd").toURI(),
				getClass().getResource("/data/shiporder/shiporder.xml").toURI());
		
		String ns = "http://www.example.com";
		
		ResourceIterator<Instance> it = instances.iterator();
		assertTrue(it.hasNext());
		
		Instance instance = it.next();
		assertNotNull(instance);
		
		Object[] orderid = instance.getProperty(new QName("orderid")); // attribute form not qualified
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
		
		//TODO items
		
		assertFalse(it.hasNext());
		
		it.dispose();
	}

	private GmlInstanceCollection loadInstances(URI schemaLocation, URI xmlLocation) throws IOException, IOProviderConfigurationException {
		SchemaReader reader = new XmlSchemaReader();
		reader.setSharedTypes(null);
		reader.setSource(new DefaultInputSupplier(schemaLocation));
		IOReport schemaReport = reader.execute(null);
		assertTrue(schemaReport.isSuccess());
		Schema sourceSchema = reader.getSchema();
		
		return new GmlInstanceCollection(
				new DefaultInputSupplier(xmlLocation), 
				sourceSchema, 
				null); //XXX content type currently not needed
	}

}
