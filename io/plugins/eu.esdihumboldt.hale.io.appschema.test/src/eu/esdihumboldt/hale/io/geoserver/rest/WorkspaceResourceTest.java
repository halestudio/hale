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

import eu.esdihumboldt.hale.io.geoserver.ResourceBuilder;
import eu.esdihumboldt.hale.io.geoserver.Workspace;

/**
 * Tests the correct serialization of a workspace resource.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class WorkspaceResourceTest extends ResourceTestBase {

	private static final String TEST_ID = "workspace-1234";
	private static final String TEST_NAME = "test_ws";

	private static final String ELEMENT_WORKSPACE = "workspace";
	private static final String ELEMENT_ID = "id";
	private static final String ELEMENT_NAME = "name";
	private static final String ELEMENT_ISOLATED = "isolated";

	/**
	 * Tests the correct serialization of a workspace resource where only
	 * {@code name} is set.
	 * 
	 * @throws Exception if an error occurs parsing the resource as XML
	 */
	@Test
	public void testSerializeDefault() throws Exception {
		Workspace testWorkspace = ResourceBuilder.workspace(TEST_NAME).build();

		Document doc = parseResource(testWorkspace);
		assertEquals(1, doc.getElementsByTagName(ELEMENT_WORKSPACE).getLength());
		Node workspaceEl = doc.getElementsByTagName(ELEMENT_WORKSPACE).item(0);

		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put(ELEMENT_NAME, TEST_NAME);
		expectedValues.put(ELEMENT_ISOLATED, "false");

		checkResource(workspaceEl, expectedValues);
	}

	/**
	 * Tests the correct serialization of a workspace resource whose
	 * {@code isolated} attribute is set to {@code true}.
	 * 
	 * @throws Exception if an error occurs parsing the resource as XML
	 */
	@Test
	public void testSerializeIsolated() throws Exception {
		Workspace testWorkspace = ResourceBuilder.workspace(TEST_NAME)
				.setAttribute(Workspace.ISOLATED, true).build();

		Document doc = parseResource(testWorkspace);
		assertEquals(1, doc.getElementsByTagName(ELEMENT_WORKSPACE).getLength());
		Node workspaceEl = doc.getElementsByTagName(ELEMENT_WORKSPACE).item(0);

		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put(ELEMENT_NAME, TEST_NAME);
		expectedValues.put(ELEMENT_ISOLATED, "true");

		checkResource(workspaceEl, expectedValues);
	}

	/**
	 * Tests the correct serialization of a workspace resource whose {@code id}
	 * attribute is set.
	 * 
	 * @throws Exception if an error occurs parsing the resource as XML
	 */
	@Test
	public void testSerializeWithId() throws Exception {
		Workspace testWorkspace = ResourceBuilder.workspace(TEST_NAME)
				.setAttribute(Workspace.ID, TEST_ID).build();

		Document doc = parseResource(testWorkspace);
		assertEquals(1, doc.getElementsByTagName(ELEMENT_WORKSPACE).getLength());
		Node workspaceEl = doc.getElementsByTagName(ELEMENT_WORKSPACE).item(0);

		Map<String, String> expectedValues = new HashMap<>();
		expectedValues.put(ELEMENT_ID, TEST_ID);
		expectedValues.put(ELEMENT_NAME, TEST_NAME);
		expectedValues.put(ELEMENT_ISOLATED, "false");

		checkResource(workspaceEl, expectedValues);
	}

}
