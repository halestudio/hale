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
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.IgnoreOrderFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeIndex;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Tests for XML schema reading
 * 
 * @author Simon Templer
 */
public class XmlSchemaReaderTest {

	/**
	 * Test reading a simple XML schema that contains one big element. Focuses
	 * on structure, simple type bindings and cardinalities.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_one() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-one.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = "http://www.example.com";
		assertEquals(ns, schema.getNamespace());

		// shiporder element
		Collection<XmlElement> elements = getElementsWithNS(ns, schema.getElements().values());
		assertEquals(1, elements.size());
		XmlElement shiporder = elements.iterator().next();

		testShiporderStructure(shiporder, ns);
	}

	/**
	 * Test reading a simple XML schema that contains one big element and where
	 * elementFormDefault/attributeFromDefault is set to unqualified and no
	 * target namespace is set. Focuses on structure, simple type bindings and
	 * cardinalities.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_unqualified() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-unqualified.xsd")
				.toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = XMLConstants.NULL_NS_URI;
		assertEquals(ns, schema.getNamespace());

		// shiporder element
		Collection<XmlElement> elements = getElementsWithNS(ns, schema.getElements().values());
		assertEquals(1, elements.size());
		XmlElement shiporder = elements.iterator().next();

		// XXX use null namespace XXX not sure how to work with unqualified form
		// FIXME target namespace no effect?! should the target namespace always
		// be injected?
		testShiporderStructure(shiporder, ns);
	}

	/**
	 * Test reading a simple XML schema that contains several elements
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_divided() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-divided.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = "http://www.example.com";
		assertEquals(ns, schema.getNamespace());

		// element count
		Collection<XmlElement> elements = getElementsWithNS(ns, schema.getElements().values());
		assertEquals(12, elements.size());
		// shiporder element
		XmlElement shiporder = schema.getElements().get(new QName(ns, "shiporder"));

		testShiporderStructure(shiporder, ns);
	}

	/**
	 * Test reading a simple XML schema that uses several custom named types
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_types() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-types.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = "http://www.example.com";
		assertEquals(ns, schema.getNamespace());

		// shiporder element
		Collection<XmlElement> elements = getElementsWithNS(ns, schema.getElements().values());
		assertEquals(1, elements.size());
		XmlElement shiporder = elements.iterator().next();

		testShiporderStructure(shiporder, ns);
	}

	/**
	 * Test reading a simple XML schema that uses several custom named types and
	 * has a cycle.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_types_cycle() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-types-cycle.xsd")
				.toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = "http://www.example.com";
		assertEquals(ns, schema.getNamespace());

		// shiporder element
		Collection<XmlElement> elements = getElementsWithNS(ns, schema.getElements().values());
		assertEquals(1, elements.size());
		XmlElement shiporder = elements.iterator().next();
		assertNotNull(shiporder);

		TypeDefinition type = shiporder.getType();
		assertEquals(5, type.getChildren().size());

		// contained shiporder element
		PropertyDefinition s2 = type.getChild(new QName(ns, "shiporder")).asProperty();
		assertNotNull(s2);
		assertEquals(type, s2.getPropertyType());
	}

	/**
	 * Test reading a simple XML schema that uses several custom named types.
	 * The types are referenced before they are declared.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_shiporder_types_reverse() throws Exception {
		URI location = getClass().getResource("/testdata/shiporder/shiporder-types-r.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		String ns = "http://www.example.com";
		assertEquals(ns, schema.getNamespace());

		// shiporder element
		Collection<XmlElement> elements = getElementsWithNS(ns, schema.getElements().values());
		assertEquals(1, elements.size());
		XmlElement shiporder = elements.iterator().next();

		testShiporderStructure(shiporder, ns);
	}

	/**
	 * Test reading a simple XML schema that uses an attribute group and an
	 * attribute with xs:date type.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_attributegroup() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/attributegroup.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// ShirtType
		TypeDefinition type = schema.getType(new QName("ShirtType"));
		assertNotNull(type);

		// not there any more because it is flattened away
//		// IdentifierGroup
//		GroupPropertyDefinition group = type.getChild(new QName("IdentifierGroup")).asGroup();
//		assertNotNull(group);
//		// not a choice
//		assertFalse(group.getConstraint(ChoiceFlag.class).isEnabled());

		// id
		PropertyDefinition id = type.getChild(new QName("id")).asProperty();
		assertNotNull(id);
		// property type must be a simple type
		assertTrue(id.getPropertyType().getConstraint(HasValueFlag.class).isEnabled());
		// binding must be string
		assertEquals(String.class, id.getPropertyType().getConstraint(Binding.class).getBinding());
		// required
		Cardinality cc = id.getConstraint(Cardinality.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());

		// version
		PropertyDefinition version = type.getChild(new QName("version")).asProperty();
		assertNotNull(version);
		// property type must be a simple type
		assertTrue(version.getPropertyType().getConstraint(HasValueFlag.class).isEnabled());

		// effDate
		PropertyDefinition effDate = type.getChild(new QName("effDate")).asProperty();
		assertNotNull(effDate);
		// binding must be compatible to Date
		assertTrue(Date.class.isAssignableFrom(
				effDate.getPropertyType().getConstraint(Binding.class).getBinding()));
	}

	/**
	 * Test reading a simple XML schema that is split into several files. Tests
	 * also the {@link XmlElements} and {@link MappingRelevantFlag} constraints
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_chapter03() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/chapter03env.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// envelope element
		XmlElement envelope = schema.getElements()
				.get(new QName("http://example.org/ord", "envelope"));
		assertNotNull(envelope);
		TypeDefinition envType = envelope.getType();
		// mappable
		assertTrue(envType.getConstraint(MappingRelevantFlag.class).isEnabled());

		// XmlElements
		Collection<? extends XmlElement> elements = envType.getConstraint(XmlElements.class)
				.getElements();
		assertEquals(1, elements.size());
		assertEquals(envelope, elements.iterator().next());

		// order
		PropertyDefinition order = envType.getChild(new QName("http://example.org/ord", "order"))
				.asProperty();
		assertNotNull(order);
		TypeDefinition orderType = order.getPropertyType();
		// mappable
		assertTrue(orderType.getConstraint(MappingRelevantFlag.class).isEnabled());

		// number
		PropertyDefinition number = orderType
				.getChild(new QName("http://example.org/ord", "number")).asProperty();
		assertNotNull(number);
		// binding must be string
		assertEquals(String.class,
				number.getPropertyType().getConstraint(Binding.class).getBinding());

		// items
		PropertyDefinition items = orderType.getChild(new QName("http://example.org/ord", "items"))
				.asProperty();
		assertNotNull(items);
		// not mappable
		assertFalse(items.getPropertyType().getConstraint(MappingRelevantFlag.class).isEnabled());
		// no elements
		assertTrue(
				items.getPropertyType().getConstraint(XmlElements.class).getElements().isEmpty());

		// SpecialOrderType
		// extension to OrderType, should be mappable using xsi:type
		TypeDefinition specialOrderType = schema
				.getType(new QName("http://example.org/ord", "SpecialOrderType"));
		assertNotNull(specialOrderType);
		// number of declared children
		assertEquals(1, specialOrderType.getDeclaredChildren().size());
		// number of children
		assertEquals(3, specialOrderType.getChildren().size());
		// mappable
		assertTrue(specialOrderType.getConstraint(MappableFlag.class).isEnabled());
		// no elements
		assertTrue(specialOrderType.getConstraint(XmlElements.class).getElements().isEmpty());

		// overall mappable types
		Collection<? extends TypeDefinition> mt = schema.getMappingRelevantTypes();
		assertEquals(2, mt.size()); // envelope, order, special order
	}

	/**
	 * Test reading a simple XML schema with choices and complex types.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_choice() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/choice_complex.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// ItemsType
		TypeDefinition itemsType = schema.getType(new QName("ItemsType"));
		assertNotNull(itemsType);

		Collection<? extends ChildDefinition<?>> children = itemsType.getChildren();
		assertEquals(1, children.size());

		// choice
		GroupPropertyDefinition choice = children.iterator().next().asGroup();
		assertNotNull(choice);
		// cardinality
		Cardinality cc = choice.getConstraint(Cardinality.class);
		assertEquals(0, cc.getMinOccurs());
		assertEquals(Cardinality.UNBOUNDED, cc.getMaxOccurs());
		// choice flag
		assertTrue(choice.getConstraint(ChoiceFlag.class).isEnabled());
		// children
		assertEquals(3, choice.getDeclaredChildren().size());

		// shirt
		PropertyDefinition shirt = choice.getChild(new QName("shirt")).asProperty();
		assertNotNull(shirt);

		// hat
		PropertyDefinition hat = choice.getChild(new QName("hat")).asProperty();
		assertNotNull(hat);

		// umbrella
		PropertyDefinition umbrella = choice.getChild(new QName("umbrella")).asProperty();
		assertNotNull(umbrella);

		// TODO extend with advanced complex type tests?
	}

	/**
	 * Test reading a simple XML schema with an "all" group and complex types.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_all() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/allgroup.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// ItemsType
		TypeDefinition itemsType = schema.getType(new QName("ItemsType"));
		assertNotNull(itemsType);

		// Ignore order flag must be set b/c of <xs:all> group
		assertTrue(itemsType.getConstraint(IgnoreOrderFlag.class).isEnabled());

		assertEquals(2, itemsType.getChildren().size());

		Iterator<? extends ChildDefinition<?>> it = itemsType.getDeclaredChildren().iterator();
		// name
		PropertyDefinition name = it.next().asProperty();
		assertNotNull(name);
		assertEquals("name", name.getName().getLocalPart());
		Cardinality nameCard = name.getConstraint(Cardinality.class);
		assertEquals(0, nameCard.getMinOccurs());
		assertEquals(1, nameCard.getMaxOccurs());

		// id
		PropertyDefinition id = it.next().asProperty();
		assertNotNull(id);
		assertEquals("id", id.getName().getLocalPart());
		Cardinality idCard = id.getConstraint(Cardinality.class);
		assertEquals(1, idCard.getMinOccurs());
		assertEquals(1, idCard.getMaxOccurs());
	}

	/**
	 * Test reading a simple XML schema with sequences that have to be grouped.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_sequencegroup() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/sequencegroup.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// ItemsType
		TypeDefinition itemsType = schema.getType(new QName("ItemsType"));
		assertNotNull(itemsType);

		// Ignore order flag must not be set
		assertFalse(itemsType.getConstraint(IgnoreOrderFlag.class).isEnabled());

		assertEquals(1, itemsType.getChildren().size());

		// sequence group
		GroupPropertyDefinition sequence = itemsType.getChildren().iterator().next().asGroup();
		assertNotNull(sequence);
		// cardinality
		Cardinality cc = sequence.getConstraint(Cardinality.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(Cardinality.UNBOUNDED, cc.getMaxOccurs());
		// choice flag (not a choice)
		assertFalse(sequence.getConstraint(ChoiceFlag.class).isEnabled());

		Iterator<? extends ChildDefinition<?>> it = sequence.getDeclaredChildren().iterator();
		// name
		PropertyDefinition name = it.next().asProperty();
		assertNotNull(name);
		assertEquals("name", name.getName().getLocalPart());

		// id
		PropertyDefinition id = it.next().asProperty();
		assertNotNull(id);
		assertEquals("id", id.getName().getLocalPart());

		// choice
		GroupPropertyDefinition choice = it.next().asGroup();
		assertNotNull(choice);
		// cardinality
		cc = choice.getConstraint(Cardinality.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
		// choice flag
		assertTrue(choice.getConstraint(ChoiceFlag.class).isEnabled());

		it = choice.getDeclaredChildren().iterator();
		// choice sequence
		GroupPropertyDefinition seqGroup = it.next().asGroup();
		assertNotNull(seqGroup);
		// choice flag (not a choice)
		assertFalse(seqGroup.getConstraint(ChoiceFlag.class).isEnabled());

		// sequence elements
		// one
		PropertyDefinition one = seqGroup.getChild(new QName("one")).asProperty();
		assertNotNull(one);
		// two
		PropertyDefinition two = seqGroup.getChild(new QName("two")).asProperty();
		assertNotNull(two);

		// choice element
		PropertyDefinition single = it.next().asProperty();
		assertNotNull(single);
		assertEquals("single", single.getName().getLocalPart());
	}

	/**
	 * Test reading a simple XML schema with an annotated element.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_annotated() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/documentation_ex.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// product element
		XmlElement product = schema.getElements().get(new QName("product"));
		assertNotNull(product);
		assertTrue(product.getDescription().contains("This element represents a product."));
	}

	/**
	 * Test reading a simple XML schema containing groups and group references.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_groups() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/groups.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// ShirtType
		TypeDefinition shirtType = schema.getType(new QName("ShirtType"));
		assertNotNull(shirtType);
		assertEquals(5, shirtType.getChildren().size());

		Iterator<? extends ChildDefinition<?>> it = shirtType.getChildren().iterator();
		// ProductPropertyGroup
		GroupPropertyDefinition prodGroup = it.next().asGroup();
		// cardinality
		Cardinality cc = prodGroup.getConstraint(Cardinality.class);
		assertEquals(0, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
		// name
		assertEquals("ProductPropertyGroup", prodGroup.getName().getLocalPart());

		assertEquals(4, prodGroup.getDeclaredChildren().size());
		Iterator<? extends ChildDefinition<?>> itProd = prodGroup.getDeclaredChildren().iterator();
		// not there any more because it is flattened away
//		// DescriptionGroup
//		GroupPropertyDefinition descGroup = itProd.next().asGroup();
//		assertNotNull(descGroup);
//		// cardinality
//		cc = descGroup.getConstraint(Cardinality.class);
//		assertEquals(1, cc.getMinOccurs());
//		assertEquals(1, cc.getMaxOccurs());
//		
//		assertEquals(2, descGroup.getDeclaredChildren().size());
//		Iterator<? extends ChildDefinition<?>> itDesc = descGroup.getDeclaredChildren().iterator();
		// description
		PropertyDefinition description = itProd.next().asProperty();
		assertNotNull(description);
		assertEquals("description", description.getName().getLocalPart());

		// comment
		PropertyDefinition comment = itProd.next().asProperty();
		assertNotNull(comment);
		assertEquals("comment", comment.getName().getLocalPart());

		// number
		PropertyDefinition number = itProd.next().asProperty();
		assertNotNull(number);
		assertEquals("number", number.getName().getLocalPart());

		// name
		PropertyDefinition name = itProd.next().asProperty();
		assertNotNull(name);
		assertEquals("name", name.getName().getLocalPart());

		// size
		PropertyDefinition size = it.next().asProperty();
		assertNotNull(size);
		assertEquals("size", size.getName().getLocalPart());
	}

	/**
	 * Test reading a simple XML schema containing substitution groups.
	 * 
	 * @throws Exception if reading the schema fails
	 */
	@Test
	public void testRead_definitive_substitution() throws Exception {
		URI location = getClass().getResource("/testdata/definitive/substgroups.xsd").toURI();
		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location);
		XmlIndex schema = (XmlIndex) readSchema(input);

		// shirt element
		XmlElement shirt = schema.getElements().get(new QName("shirt"));
		assertNotNull(shirt);
		assertEquals(new QName("product"), shirt.getSubstitutionGroup());

		// TODO extend
	}

//	/**
//	 * Test reading a simple XML schema containing union and list types.
//	 * @throws Exception if reading the schema fails
//	 */
//	@Test
//	public void testRead_definitive_unionlist() throws Exception {
//		URI location = getClass().getResource("/testdata/definitive/unionlist.xsd").toURI();
//		LocatableInputSupplier<? extends InputStream> input = new DefaultInputSupplier(location );
//		XmlIndex schema = (XmlIndex) readSchema(input);
//		
//		//TODO create tests
//	 }

	private Collection<XmlElement> getElementsWithNS(final String ns,
			Collection<XmlElement> values) {
		return Collections2.filter(values, new Predicate<XmlElement>() {

			@Override
			public boolean apply(XmlElement input) {
				return Objects.equal(ns, input.getName().getNamespaceURI());
			}

		});
	}

	/**
	 * Reads a schema
	 * 
	 * @param input the input supplier
	 * @return the schema
	 * @throws IOProviderConfigurationException if the configuration of the
	 *             reader is invalid
	 * @throws IOException if reading the schema fails
	 */
	public static Schema readSchema(LocatableInputSupplier<? extends InputStream> input)
			throws IOProviderConfigurationException, IOException {
		XmlSchemaReader reader = new XmlSchemaReader();
//		reader.setContentType(XMLSchemaIO.XSD_CT);
		reader.setSharedTypes(new DefaultTypeIndex());
		reader.setSource(input);

		reader.validate();
		IOReport report = reader.execute(null);

		assertTrue(report.isSuccess());
		assertTrue("Errors are contained in the report", report.getErrors().isEmpty());

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
		PropertyDefinition orderperson = shiporderType.getChild(new QName(ns, "orderperson"))
				.asProperty();
		assertNotNull(orderperson);
		// property type must be a simple type
		assertTrue(orderperson.getPropertyType().getConstraint(HasValueFlag.class).isEnabled());
		// binding must be string
		assertEquals(String.class,
				orderperson.getPropertyType().getConstraint(Binding.class).getBinding());
		// cardinality
		Cardinality cc = orderperson.getConstraint(Cardinality.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
		// not nillable
		assertFalse(orderperson.getConstraint(NillableFlag.class).isEnabled());

		// shipto
		PropertyDefinition shipto = shiporderType.getChild(new QName(ns, "shipto")).asProperty();
		assertNotNull(shipto);
		// property type must be a complex type
		assertFalse(shipto.getPropertyType().getConstraint(HasValueFlag.class).isEnabled());

		// item
		PropertyDefinition item = shiporderType.getChild(new QName(ns, "item")).asProperty();
		assertNotNull(item);
		// property type must be a complex type
		assertFalse(item.getPropertyType().getConstraint(HasValueFlag.class).isEnabled());
		// item cardinality
		cc = item.getConstraint(Cardinality.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(Cardinality.UNBOUNDED, cc.getMaxOccurs());

		// item properties
		TypeDefinition itemType = item.getPropertyType();
		Collection<? extends ChildDefinition<?>> itemProps = itemType.getChildren();
		assertEquals(4, itemProps.size());
		// title
		assertNotNull(itemType.getChild(new QName(ns, "title")).asProperty());
		// note
		PropertyDefinition note = itemType.getChild(new QName(ns, "note")).asProperty();
		assertNotNull(note);
		cc = note.getConstraint(Cardinality.class);
		assertEquals(0, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
		// quantity
		PropertyDefinition quantity = itemType.getChild(new QName(ns, "quantity")).asProperty();
		assertNotNull(quantity);
		assertTrue(quantity.getPropertyType().getConstraint(HasValueFlag.class).isEnabled());
		assertTrue(Number.class.isAssignableFrom(
				quantity.getPropertyType().getConstraint(Binding.class).getBinding()));
		// price
		PropertyDefinition price = itemType.getChild(new QName(ns, "price")).asProperty();
		assertNotNull(price);
		assertTrue(price.getPropertyType().getConstraint(HasValueFlag.class).isEnabled());
		assertTrue(Number.class.isAssignableFrom(
				price.getPropertyType().getConstraint(Binding.class).getBinding()));

		// orderid
		PropertyDefinition orderid = shiporderType.getChild(new QName(ns, "orderid")).asProperty();
		assertNotNull(orderid);
		// binding must be string
		assertEquals(String.class,
				orderid.getPropertyType().getConstraint(Binding.class).getBinding());
		// required
		cc = orderid.getConstraint(Cardinality.class);
		assertEquals(1, cc.getMinOccurs());
		assertEquals(1, cc.getMaxOccurs());
	}

}
