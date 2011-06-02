/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.xsd.reader;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.io.xsd.XmlSchemaIO;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlElement;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlIndex;
import eu.esdihumboldt.hale.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraints.property.CardinalityConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.property.NillableFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.BindingConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.type.SimpleFlag;
import eu.esdihumboldt.hale.schema.model.impl.DefaultTypeIndex;

/**
 * Tests for XML schema reading
 * @author Simon Templer
 */
public class XmlSchemaReaderTest {

	/**
	 * Test reading a simple XML schema that contains one big element
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_one() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-one.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
		XmlIndex schema = (XmlIndex) readSchema(input);
		
		String ns = "http://www.example.com";
		assertEquals(ns , schema.getNamespace());
		
		// shiporder element
		assertEquals(1, schema.getElements().size());
		XmlElement shiporder = schema.getElements().values().iterator().next();
		assertNotNull(shiporder);
		assertEquals("shiporder", shiporder.getName().getLocalPart());
		
		// shiporder type
		TypeDefinition shiporderType = shiporder.getType();
		assertNotNull(shiporderType);
		
		Collection<? extends PropertyDefinition> properties = shiporderType.getProperties();
		assertEquals(4, properties.size());
		
		// orderperson
		PropertyDefinition orderperson = shiporderType.getProperty(new QName(ns, "orderperson"));
		assertNotNull(orderperson);
		// property type must be a simple type
		assertTrue(orderperson.getPropertyType().getConstraint(
				SimpleFlag.class).isEnabled());
		// binding must be string
		assertEquals(String.class, orderperson.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding());
		// cardinality
		CardinalityConstraint cc = orderperson.getConstraint(CardinalityConstraint.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
		// not nillable
		assertFalse(orderperson.getConstraint(NillableFlag.class).isEnabled());
		
		// shipto
		PropertyDefinition shipto = shiporderType.getProperty(new QName(ns, "shipto"));
		assertNotNull(shipto);
		// property type must be a complex type
		assertFalse(shipto.getPropertyType().getConstraint(
				SimpleFlag.class).isEnabled());
		// binding must be Instance
		assertEquals(Instance.class, shipto.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding());
		
		// item
		PropertyDefinition item = shiporderType.getProperty(new QName(ns, "item"));
		assertNotNull(item);
		
		// orderid
		PropertyDefinition orderid = shiporderType.getProperty(new QName("orderid"));
		assertNotNull(orderid);
		// binding must be string
		assertEquals(String.class, orderid.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding());
		// required
		cc = orderid.getConstraint(CardinalityConstraint.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
	}
	
	/**
	 * Test reading a simple XML schema that contains several elements
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_divided() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-divided.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
		XmlIndex schema = (XmlIndex) readSchema(input);
		
		String ns = "http://www.example.com";
		assertEquals(ns , schema.getNamespace());
		
		//TODO extend
	}
	
	/**
	 * Test reading a simple XML schema that uses several custom named types
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_types() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-types.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
		XmlIndex schema = (XmlIndex) readSchema(input);
		
		String ns = "http://www.example.com";
		assertEquals(ns , schema.getNamespace());
		
		//TODO extend
	}
	
	/**
	 * Reads a schema
	 * 
	 * @param input the input supplier
	 * @return the schema
	 * @throws IOProviderConfigurationException if the configuration of the
	 *   reader is invalid
	 * @throws IOException if reading the schema fails
	 */
	private Schema readSchema(LocatableInputSupplier<? extends InputStream> input) throws IOProviderConfigurationException, IOException {
		XmlSchemaReader reader = new XmlSchemaReader();
		reader.setContentType(XmlSchemaIO.XSD_CT);
		reader.setSharedTypes(new DefaultTypeIndex());
		reader.setSource(input);
		
		reader.validate();
		reader.execute(null);
		
		return reader.getSchema();
	}
	
}
