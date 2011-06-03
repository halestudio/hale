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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.junit.Test;

import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.io.xsd.XmlSchemaIO;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlElement;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.GroupPropertyDefinition;
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
	 * Test reading a simple XML schema that contains one big element.
	 * Focuses on structure, simple type bindings and cardinalities.
	 * 
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
		
		testShiporderStructure(shiporder, ns);
	}
	
	/**
	 * Test reading a simple XML schema that contains one big element and where
	 * elementFormDefault/attributeFromDefault is set to unqualified and no
	 * target namespace is set.
	 * Focuses on structure, simple type bindings and cardinalities.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_unqualified() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-unqualified.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
		XmlIndex schema = (XmlIndex) readSchema(input);
		
		String ns = XMLConstants.NULL_NS_URI;
		assertEquals(ns , schema.getNamespace());
		
		// shiporder element
		assertEquals(1, schema.getElements().size());
		XmlElement shiporder = schema.getElements().values().iterator().next();
		
		//XXX use null namespace XXX not sure how to work with unqualified form
		//FIXME target namespace no effect?! should the target namespace always be injected?
		testShiporderStructure(shiporder, ns);
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
		
		// element count
		assertEquals(12, schema.getElements().size());
		// shiporder element
		XmlElement shiporder = schema.getElements().get(new QName(ns, "shiporder"));
		
		testShiporderStructure(shiporder, ns);
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
		
		// shiporder element
		assertEquals(1, schema.getElements().size());
		XmlElement shiporder = schema.getElements().values().iterator().next();
		
		testShiporderStructure(shiporder, ns);
	}
	
	/**
	 * Test reading a simple XML schema that uses several custom named types.
	 * The types are referenced before they are declared.
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_types_reverse() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-types-r.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
		XmlIndex schema = (XmlIndex) readSchema(input);
		
		String ns = "http://www.example.com";
		assertEquals(ns , schema.getNamespace());
		
		// shiporder element
		assertEquals(1, schema.getElements().size());
		XmlElement shiporder = schema.getElements().values().iterator().next();
		
		testShiporderStructure(shiporder, ns);
	}
	
	/**
	 * Test reading a simple XML schema that uses an attribute group and an
	 * attribute with xs:date type.
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_attributegroup() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/attributegroup.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
		XmlIndex schema = (XmlIndex) readSchema(input);
		
		// ShirtType
		XmlTypeDefinition type = schema.getType(new QName("ShirtType"));
		assertNotNull(type);
		
		// IdentifierGroup
		GroupPropertyDefinition group = type.getChild(new QName("IdentifierGroup")).asGroup();
		assertNotNull(group);
		
		// id
		PropertyDefinition id = group.getChild(new QName("id")).asProperty();
		assertNotNull(id);
		// property type must be a simple type
		assertTrue(id.getPropertyType().getConstraint(
				SimpleFlag.class).isEnabled());
		// binding must be string
		assertEquals(String.class, id.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding());
		// required
		CardinalityConstraint cc = id.getConstraint(CardinalityConstraint.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
		
		// version
		PropertyDefinition version = group.getChild(new QName("version")).asProperty();
		assertNotNull(version);
		// property type must be a simple type
		assertTrue(version.getPropertyType().getConstraint(
				SimpleFlag.class).isEnabled());
		
		// effDate
		PropertyDefinition effDate = type.getChild(new QName("effDate")).asProperty();
		assertNotNull(effDate);
		// binding must be compatible to Date
		assertTrue(Date.class.isAssignableFrom(effDate.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding()));
	}
	
	/**
	 * Test reading a simple XML schema that is split into several files.
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_chapter03() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/chapter03env.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
		XmlIndex schema = (XmlIndex) readSchema(input);
		
		// envelope element
		XmlElement envelope = schema.getElements().get(new QName("envelope"));
		assertNotNull(envelope);
		TypeDefinition envType = envelope.getType();
		
		// order
		PropertyDefinition order = envType.getChild(new QName(
				"http://example.org/ord", "order")).asProperty();
		assertNotNull(order);
		TypeDefinition orderType = order.getPropertyType();
		
		// number
		PropertyDefinition number = orderType.getChild(new QName(
				"http://example.org/ord", "number")).asProperty();
		assertNotNull(number);
		// binding must be string
		assertEquals(String.class, number.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding());
		
		// items
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

	/**
	 * Test the shiporder structure
	 * 
	 * @param shiporder the shiporder element
	 * @param ns the namespace
	 */
	private void testShiporderStructure(XmlElement shiporder, String ns) {
		assertNotNull(shiporder);
		assertEquals("shiporder", shiporder.getName().getLocalPart());
		
		// shiporder type
		TypeDefinition shiporderType = shiporder.getType();
		assertNotNull(shiporderType);
		
		Collection<? extends ChildDefinition<?>> properties = shiporderType.getChildren();
		assertEquals(4, properties.size());
		
		// orderperson
		PropertyDefinition orderperson = shiporderType.getChild(new QName(ns, "orderperson")).asProperty();
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
		PropertyDefinition shipto = shiporderType.getChild(new QName(ns, "shipto")).asProperty();
		assertNotNull(shipto);
		// property type must be a complex type
		assertFalse(shipto.getPropertyType().getConstraint(
				SimpleFlag.class).isEnabled());
		// binding must be Instance
		assertEquals(Instance.class, shipto.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding());
		
		// item
		PropertyDefinition item = shiporderType.getChild(new QName(ns, "item")).asProperty();
		assertNotNull(item);
		// property type must be a complex type
		assertFalse(item.getPropertyType().getConstraint(
				SimpleFlag.class).isEnabled());
		// item cardinality
		cc = item.getConstraint(CardinalityConstraint.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(CardinalityConstraint.UNBOUNDED, cc.getMaxOccurs());
		
		// item properties
		TypeDefinition itemType = item.getPropertyType();
		Collection<? extends ChildDefinition<?>> itemProps = itemType.getChildren();
		assertEquals(4, itemProps.size());
		// title
		assertNotNull(itemType.getChild(new QName(ns, "title")).asProperty());
		// note
		PropertyDefinition note = itemType.getChild(new QName(ns, "note")).asProperty();
		assertNotNull(note);
		cc = note.getConstraint(CardinalityConstraint.class);
		assertEquals(0, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
		// quantity
		PropertyDefinition quantity = itemType.getChild(new QName(ns, "quantity")).asProperty();
		assertNotNull(quantity);
		assertTrue(quantity.getPropertyType().getConstraint(SimpleFlag.class).isEnabled());
		assertTrue(Number.class.isAssignableFrom(quantity.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding()));
		// price
		PropertyDefinition price = itemType.getChild(new QName(ns, "price")).asProperty();
		assertNotNull(price);
		assertTrue(price.getPropertyType().getConstraint(SimpleFlag.class).isEnabled());
		assertTrue(Number.class.isAssignableFrom(price.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding()));
		
		// orderid
		PropertyDefinition orderid = shiporderType.getChild(new QName(ns, "orderid")).asProperty();
		assertNotNull(orderid);
		// binding must be string
		assertEquals(String.class, orderid.getPropertyType().getConstraint(
				BindingConstraint.class).getBinding());
		// required
		cc = orderid.getConstraint(CardinalityConstraint.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
	}
	
}
