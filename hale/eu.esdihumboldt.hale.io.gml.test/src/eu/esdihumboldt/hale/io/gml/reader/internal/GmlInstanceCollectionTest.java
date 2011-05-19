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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import eu.esdihumboldt.hale.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.SchemaProvider;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

import static org.junit.Assert.*;


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
		
		ResourceIterator<Instance> it = instances.iterator();
		assertTrue(it.hasNext());
		
		Instance instance = it.next();
		assertNotNull(instance);
		
		Object[] orderid = instance.getProperty("orderid");
		assertNotNull(orderid);
		assertEquals(1, orderid.length);
		assertEquals("889923", orderid[0]);
		
		Object[] orderperson = instance.getProperty("orderperson");
		assertNotNull(orderperson);
		assertEquals(1, orderperson.length);
		assertEquals("John Smith", orderperson[0]);
		
		Object[] shipto = instance.getProperty("shipto");
		assertNotNull(shipto);
		assertEquals(1, shipto.length);
		assertTrue(shipto[0] instanceof Instance);
		Instance shipto1 = (Instance) shipto[0];
		
		Object[] shiptoName = shipto1.getProperty("name");
		assertNotNull(shiptoName);
		assertEquals(1, shiptoName.length);
		assertEquals("Ola Nordmann", shiptoName[0]);
		
		Object[] shiptoAddress = shipto1.getProperty("address");
		assertNotNull(shiptoAddress);
		assertEquals(1, shiptoAddress.length);
		assertEquals("Langgt 23", shiptoAddress[0]);
		
		Object[] shiptoCity = shipto1.getProperty("city");
		assertNotNull(shiptoCity);
		assertEquals(1, shiptoCity.length);
		assertEquals("4000 Stavanger", shiptoCity[0]);
		
		Object[] shiptoCountry = shipto1.getProperty("country");
		assertNotNull(shiptoCountry);
		assertEquals(1, shiptoCountry.length);
		assertEquals("Norway", shiptoCountry[0]);
		
		//TODO items
		
		assertFalse(it.hasNext());
		
		it.dispose();
	}

	private GmlInstanceCollection loadInstances(URI schemaLocation, URI xmlLocation) throws IOException {
		SchemaProvider sp = new ApacheSchemaProvider();
		Schema sourceSchema = sp.loadSchema(schemaLocation, null);
		
		return new GmlInstanceCollection(
				new DefaultInputSupplier(xmlLocation), 
				sourceSchema, 
				null); //XXX content type currently not needed
	}

}
