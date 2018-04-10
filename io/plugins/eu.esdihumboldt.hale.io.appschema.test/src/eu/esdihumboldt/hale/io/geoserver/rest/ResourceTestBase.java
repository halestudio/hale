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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import eu.esdihumboldt.hale.io.geoserver.Resource;

/**
 * Base class with utility methods for testing the correct serialization of
 * GeoServer resources.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class ResourceTestBase {

	/**
	 * Document builder factory used to parse XML files.
	 */
	protected static final DocumentBuilderFactory dbf;

	static {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
	}

	/**
	 * Parses a resource's XML representation.
	 * 
	 * @param resource the resource to parse
	 * @return the {@link Document} obtained by parsing the resource
	 * @throws Exception if an error occurs parsing the resource
	 */
	protected Document parseResource(Resource resource) throws Exception {
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		return docBuilder.parse(resource.asStream());
	}

	/**
	 * Checks that a resource elements contains the exact sub-elements specified
	 * in the {@code expectedAttributes} map, in which a key, value pair
	 * correspond to a sub-element's name and value.
	 * 
	 * @param resourceEl the resource element
	 * @param expectedSubElements the expected sub-elements
	 */
	protected void checkResource(Node resourceEl, Map<String, String> expectedSubElements) {
		Map<String, Boolean> attributeFound = new HashMap<>();
		// no attribute found, yet
		expectedSubElements.forEach((k, v) -> attributeFound.put(k, false));

		for (int i = 0; i < resourceEl.getChildNodes().getLength(); i++) {
			Node child = resourceEl.getChildNodes().item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String childName = child.getNodeName();
				if (!expectedSubElements.containsKey(childName)) {
					fail("attribute \"" + childName + "\" was not expected");
				}
				attributeFound.put(childName, checkElement(child,
						expectedSubElements.get(childName), attributeFound.get(childName)));
			}
			else if (child.getNodeType() == Node.TEXT_NODE) {
				// test it's whitespace
				assertTrue(child.getTextContent().trim().isEmpty());
			}
			else {
				fail("unexpected node type found: " + child.getNodeType());
			}
		}

		attributeFound.forEach((name,
				found) -> assertTrue("expected attribute \"" + name + "\" was not found", found));
	}

	private boolean checkElement(Node element, String content, boolean found) {
		if (found) {
			fail(element.getNodeName() + " element found twice");
		}
		assertEquals(content, element.getTextContent().trim());

		return true;
	}

}
