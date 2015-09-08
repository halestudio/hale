/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.appschema.AppSchemaIO;

/**
 * Tests {@link FeatureChainingComplexType} serialization / deserialization.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class FeatureChainingComplexTypeTest {

	private static final String TEST_DATA = "/data/feature-chaining-value.xml";
	private static final String GEOSCIML_NS = "urn:cgi:xmlns:CGI:GeoSciML:2.0";
	private static final String GEOLOGIC_UNIT_TYPE = "GeologicUnitType";

	/**
	 * Test reading a feature chaining configuration from XML.
	 * 
	 * @throws Exception if an error occurs during the test
	 */
	@Test
	public void testRead() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Element root = builder.parse(getClass().getResourceAsStream(TEST_DATA))
				.getDocumentElement();

		// read
		FeatureChaining featureChaining = HaleIO.getComplexValue(root, FeatureChaining.class, null);

		assertNotNull(featureChaining);

		Map<String, JoinConfiguration> joins = featureChaining.getJoins();
		assertNotNull(joins);
		assertEquals(1, joins.size());

		JoinConfiguration join = joins.get("Cd4e57fb5-a411-4721-a29a-19343b853a12");
		assertNotNull(join);

		Map<Integer, ChainConfiguration> chains = join.getChains();
		assertNotNull(chains);
		assertEquals(5, chains.size());

		ChainConfiguration chain0 = chains.get(0);
		assertEquals(0, chain0.getChainIndex());
		assertEquals(-1, chain0.getPrevChainIndex());
		assertTrue(chain0.getMappingName() == null);

		PropertyType target0 = chain0.getJaxbNestedTypeTarget();
		assertNotNull(target0);
		assertEquals(GEOLOGIC_UNIT_TYPE, target0.getType().getName());
		assertEquals(GEOSCIML_NS, target0.getType().getNs());
		assertNotNull(target0.getChild());
		assertEquals(2, target0.getChild().size());
		assertEquals("composition", target0.getChild().get(0).getName());
		assertEquals(GEOSCIML_NS, target0.getChild().get(0).getNs());
		assertEquals("CompositionPart", target0.getChild().get(1).getName());
		assertEquals(GEOSCIML_NS, target0.getChild().get(1).getNs());

		ChainConfiguration chain1 = chains.get(1);
		assertEquals(1, chain1.getChainIndex());
		assertEquals(-1, chain1.getPrevChainIndex());
		assertTrue(chain1.getMappingName() == null);

		PropertyType target1 = chain1.getJaxbNestedTypeTarget();
		assertNotNull(target1);
		assertEquals(GEOLOGIC_UNIT_TYPE, target1.getType().getName());
		assertEquals(GEOSCIML_NS, target1.getType().getNs());
		assertNotNull(target1.getChild());
		assertEquals(3, target1.getChild().size());
		assertEquals("occurrence", target1.getChild().get(0).getName());
		assertEquals(GEOSCIML_NS, target1.getChild().get(0).getNs());
		assertEquals("choice", target1.getChild().get(1).getName());
		assertEquals("urn:cgi:xmlns:CGI:GeoSciML:2.0/MappedFeature", target1.getChild().get(1)
				.getNs());
		assertEquals("MappedFeature", target1.getChild().get(2).getName());
		assertEquals(GEOSCIML_NS, target1.getChild().get(2).getNs());

		ChainConfiguration chain2 = chains.get(2);
		assertEquals(2, chain2.getChainIndex());
		assertEquals(-1, chain2.getPrevChainIndex());
		assertTrue(chain2.getMappingName() == null);

		PropertyType target2 = chain2.getJaxbNestedTypeTarget();
		assertNotNull(target2);
		assertEquals(GEOLOGIC_UNIT_TYPE, target2.getType().getName());
		assertEquals(GEOSCIML_NS, target2.getType().getNs());
		assertNotNull(target2.getChild());
		assertEquals(3, target2.getChild().size());
		assertEquals("geologicHistory", target2.getChild().get(0).getName());
		assertEquals(GEOSCIML_NS, target2.getChild().get(0).getNs());
		assertEquals("choice", target2.getChild().get(1).getName());
		assertEquals("urn:cgi:xmlns:CGI:GeoSciML:2.0/GeologicEvent", target2.getChild().get(1)
				.getNs());
		assertEquals("GeologicEvent", target2.getChild().get(2).getName());
		assertEquals(GEOSCIML_NS, target2.getChild().get(2).getNs());

		ChainConfiguration chain3 = chains.get(3);
		assertEquals(3, chain3.getChainIndex());
		assertEquals(2, chain3.getPrevChainIndex());
		assertEquals("c41266fa-ca85-46bb-b93f-e38d9abec0c7", chain3.getMappingName());

		PropertyType target3 = chain3.getJaxbNestedTypeTarget();
		assertNotNull(target3);
		assertEquals(GEOLOGIC_UNIT_TYPE, target3.getType().getName());
		assertEquals(GEOSCIML_NS, target3.getType().getNs());
		assertNotNull(target3.getChild());
		assertEquals(5, target3.getChild().size());
		assertEquals("geologicHistory", target3.getChild().get(0).getName());
		assertEquals(GEOSCIML_NS, target3.getChild().get(0).getNs());
		assertEquals("choice", target3.getChild().get(1).getName());
		assertEquals("urn:cgi:xmlns:CGI:GeoSciML:2.0/GeologicEvent", target3.getChild().get(1)
				.getNs());
		assertEquals("GeologicEvent", target3.getChild().get(2).getName());
		assertEquals(GEOSCIML_NS, target3.getChild().get(2).getNs());
		assertEquals("eventProcess", target3.getChild().get(3).getName());
		assertEquals(GEOSCIML_NS, target3.getChild().get(3).getNs());
		assertEquals("CGI_TermValue", target3.getChild().get(4).getName());
		assertEquals(GEOSCIML_NS, target3.getChild().get(4).getNs());

		ChainConfiguration chain4 = chains.get(4);
		assertEquals(4, chain4.getChainIndex());
		assertEquals(2, chain4.getPrevChainIndex());
		assertEquals("cbb4baa5-79d9-4012-95d8-d1574628761b", chain4.getMappingName());

		PropertyType target4 = chain4.getJaxbNestedTypeTarget();
		assertNotNull(target4);
		assertEquals(GEOLOGIC_UNIT_TYPE, target4.getType().getName());
		assertEquals(GEOSCIML_NS, target4.getType().getNs());
		assertNotNull(target4.getChild());
		assertEquals(5, target4.getChild().size());
		assertEquals("geologicHistory", target4.getChild().get(0).getName());
		assertEquals(GEOSCIML_NS, target4.getChild().get(0).getNs());
		assertEquals("choice", target4.getChild().get(1).getName());
		assertEquals("urn:cgi:xmlns:CGI:GeoSciML:2.0/GeologicEvent", target4.getChild().get(1)
				.getNs());
		assertEquals("GeologicEvent", target4.getChild().get(2).getName());
		assertEquals(GEOSCIML_NS, target4.getChild().get(2).getNs());
		assertEquals("eventEnvironment", target4.getChild().get(3).getName());
		assertEquals(GEOSCIML_NS, target4.getChild().get(3).getNs());
		assertEquals("CGI_TermValue", target4.getChild().get(4).getName());
		assertEquals(GEOSCIML_NS, target4.getChild().get(4).getNs());
	}

	/**
	 * Tests converting a feature chaining configuration to DOM and back.
	 */
	@Test
	public void testBackAndForth() {
		FeatureChaining testConf = new FeatureChaining();

		TypeDefinition fakeType = new DefaultTypeDefinition(new QName(
				AppSchemaIO.APP_SCHEMA_NAMESPACE, "FakeType"));
		PropertyDefinition fakeProperty0 = new DefaultPropertyDefinition(new QName(
				AppSchemaIO.APP_SCHEMA_NAMESPACE, "fakeProperty0"), fakeType,
				new DefaultTypeDefinition(new QName("FakeNestedType0PropertyType")));
		PropertyDefinition fakeProperty1 = new DefaultPropertyDefinition(new QName(
				AppSchemaIO.APP_SCHEMA_NAMESPACE, "FakeNestedType0"), fakeType,
				new DefaultTypeDefinition(new QName("FakeNestedType0Type")));
		List<ChildContext> path0 = Arrays.asList(new ChildContext[] {
				new ChildContext(fakeProperty0), new ChildContext(fakeProperty1) });

		ChainConfiguration chain0 = new ChainConfiguration();
		chain0.setChainIndex(0);
		chain0.setPrevChainIndex(-1);
		chain0.setNestedTypeTarget(new PropertyEntityDefinition(fakeType, path0,
				SchemaSpaceID.TARGET, null));

		PropertyDefinition fakeProperty2 = new DefaultPropertyDefinition(new QName(
				AppSchemaIO.APP_SCHEMA_NAMESPACE, "fakeProperty1"), fakeType,
				new DefaultTypeDefinition(new QName("FakeNestedType1PropertyType")));
		PropertyDefinition fakeProperty3 = new DefaultPropertyDefinition(new QName(
				AppSchemaIO.APP_SCHEMA_NAMESPACE, "FakeNestedType1"), fakeType,
				new DefaultTypeDefinition(new QName("FakeNestedType1Type")));
		List<ChildContext> path1 = Arrays.asList(new ChildContext[] {
				new ChildContext(fakeProperty0), new ChildContext(fakeProperty1),
				new ChildContext(fakeProperty2), new ChildContext(fakeProperty3) });

		ChainConfiguration chain1 = new ChainConfiguration();
		chain1.setChainIndex(1);
		chain1.setPrevChainIndex(0);
		chain1.setNestedTypeTarget(new PropertyEntityDefinition(fakeType, path1,
				SchemaSpaceID.TARGET, null));
		chain1.setMappingName("fakeMapping");

		testConf.putChain("test-join", 0, chain0);
		testConf.putChain("test-join", 1, chain1);

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(testConf);

		// convert back
		FeatureChaining converted = HaleIO.getComplexValue(fragment, FeatureChaining.class, null);
		assertNotNull(converted);
		assertFalse(converted.equals(testConf));

		Map<String, JoinConfiguration> joins = converted.getJoins();
		assertNotNull(joins);
		assertEquals(1, joins.size());

		JoinConfiguration join = joins.get("test-join");
		assertNotNull(join);
		assertEquals(2, join.getChains().size());

		ChainConfiguration convChain0 = join.getChain(0);
		assertNotNull(convChain0);
		assertEquals(0, convChain0.getChainIndex());
		assertEquals(-1, convChain0.getPrevChainIndex());
		assertTrue(convChain0.getMappingName() == null);

		PropertyType convPropertyType0 = convChain0.getJaxbNestedTypeTarget();
		assertNotNull(convPropertyType0);
		assertEquals("FakeType", convPropertyType0.getType().getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType0.getType().getNs());
		assertEquals(2, convPropertyType0.getChild().size());
		assertEquals("fakeProperty0", convPropertyType0.getChild().get(0).getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType0.getChild().get(0).getNs());
		assertEquals("FakeNestedType0", convPropertyType0.getChild().get(1).getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType0.getChild().get(1).getNs());

		ChainConfiguration convChain1 = join.getChain(1);
		assertNotNull(convChain1);
		assertEquals(1, convChain1.getChainIndex());
		assertEquals(0, convChain1.getPrevChainIndex());
		assertEquals("fakeMapping", convChain1.getMappingName());

		PropertyType convPropertyType1 = convChain1.getJaxbNestedTypeTarget();
		assertNotNull(convPropertyType1);
		assertEquals("FakeType", convPropertyType1.getType().getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType1.getType().getNs());
		assertEquals(4, convPropertyType1.getChild().size());
		assertEquals("fakeProperty0", convPropertyType1.getChild().get(0).getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType1.getChild().get(0).getNs());
		assertEquals("FakeNestedType0", convPropertyType1.getChild().get(1).getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType1.getChild().get(1).getNs());
		assertEquals("fakeProperty1", convPropertyType1.getChild().get(2).getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType1.getChild().get(2).getNs());
		assertEquals("FakeNestedType1", convPropertyType1.getChild().get(3).getName());
		assertEquals(AppSchemaIO.APP_SCHEMA_NAMESPACE, convPropertyType1.getChild().get(3).getNs());
	}
}
