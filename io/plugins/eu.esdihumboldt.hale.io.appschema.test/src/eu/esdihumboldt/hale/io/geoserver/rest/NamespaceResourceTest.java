/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     GeoSolutions <https://www.geo-solutions.it>
 */

package eu.esdihumboldt.hale.io.geoserver.rest;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.esdihumboldt.hale.io.geoserver.Namespace;
import eu.esdihumboldt.hale.io.geoserver.ResourceBuilder;

/**
 * Tests the correct serialization of a namespace resource.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class NamespaceResourceTest extends ResourceTestBase {

	private static final String TEST_ID = "namespace-1234";
	private static final String TEST_PREFIX = "test_ns";
	private static final String TEST_URI = "http://www.example.com";

	private static final String ELEMENT_NAMESPACE = "namespace";
	private static final String ELEMENT_ID = "id";
	private static final String ELEMENT_PREFIX = "prefix";
	private static final String ELEMENT_URI = "uri";
	private static final String ELEMENT_ISOLATED = "isolated";

	/**
	 * Tests the correct serialization of a namespace resource where only
	 * {@code prefix} and {@code uri} are set.
	 * 
	 * @throws Exception if an error occurs parsing the resource as XML
	 */
	@Test
	public void testSerializeDefault() throws Exception {
		Namespace testNamespace = ResourceBuilder.namespace(TEST_PREFIX)
				.setAttribute(Namespace.URI, TEST_URI).build();

		Document doc = parseResource(testNamespace);
		assertEquals(1, doc.getElementsByTagName(ELEMENT_NAMESPACE).getLength());
		Node namespaceEl = doc.getElementsByTagName(ELEMENT_NAMESPACE).item(0);

		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put(ELEMENT_PREFIX, TEST_PREFIX);
		expectedValues.put(ELEMENT_URI, TEST_URI);
		expectedValues.put(ELEMENT_ISOLATED, "false");

		checkResource(namespaceEl, expectedValues);
	}

	/**
	 * Tests the correct serialization of a namespace resource whose
	 * {@code isolated} attribute is set to {@code true}.
	 * 
	 * @throws Exception if an error occurs parsing the resource as XML
	 */
	@Test
	public void testSerializeIsolated() throws Exception {
		Namespace testNamespace = ResourceBuilder.namespace(TEST_PREFIX)
				.setAttribute(Namespace.URI, TEST_URI).setAttribute(Namespace.ISOLATED, true)
				.build();

		Document doc = parseResource(testNamespace);
		assertEquals(1, doc.getElementsByTagName(ELEMENT_NAMESPACE).getLength());
		Node namespaceEl = doc.getElementsByTagName(ELEMENT_NAMESPACE).item(0);

		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put(ELEMENT_PREFIX, TEST_PREFIX);
		expectedValues.put(ELEMENT_URI, TEST_URI);
		expectedValues.put(ELEMENT_ISOLATED, "true");

		checkResource(namespaceEl, expectedValues);
	}

	/**
	 * Tests the correct serialization of a namespace resource whose {@code id}
	 * attribute is set.
	 * 
	 * @throws Exception if an error occurs parsing the resource as XML
	 */
	@Test
	public void testSerializeWithId() throws Exception {
		Namespace testNamespace = ResourceBuilder.namespace(TEST_PREFIX)
				.setAttribute(Namespace.URI, TEST_URI).setAttribute(Namespace.ID, TEST_ID).build();

		Document doc = parseResource(testNamespace);
		assertEquals(1, doc.getElementsByTagName(ELEMENT_NAMESPACE).getLength());
		Node namespaceEl = doc.getElementsByTagName(ELEMENT_NAMESPACE).item(0);

		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put(ELEMENT_ID, TEST_ID);
		expectedValues.put(ELEMENT_PREFIX, TEST_PREFIX);
		expectedValues.put(ELEMENT_URI, TEST_URI);
		expectedValues.put(ELEMENT_ISOLATED, "false");

		checkResource(namespaceEl, expectedValues);
	}

}
