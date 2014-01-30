/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;

/**
 * Tests {@link ValueList} serialization.
 * 
 * @author Simon Templer
 */
public class ValueListTypeTest {

	private final String XML_SAMPLE = "<core:list xmlns:core=\"http://www.esdi-humboldt.eu/hale/core\">" //
			+ "<entry value=\"foo\" />" //
			+ "<entry value=\"bar\" />" //
			+ "<entry>" //
			+ "<core:list><entry value=\"hello\"/></core:list>" //
			+ "</entry>" //
			+ "</core:list>";

	/**
	 * Test reading a value list from XML.
	 */
	@SuppressWarnings("javadoc")
	@Test
	public void testRead() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(XML_SAMPLE.getBytes());
		Element root = builder.parse(inputStream).getDocumentElement();

		// read
		ValueList conv = HaleIO.getComplexValue(root, ValueList.class, null);

		assertEquals(3, conv.size());
		assertEquals("foo", conv.get(0).as(String.class));
		assertEquals("bar", conv.get(1).as(String.class));
		assertEquals("hello", conv.get(2).as(ValueList.class).get(0).as(String.class));
	}

	/**
	 * Test if a simple list containing only {@link StringValue}s is the same
	 * when converted to DOM and back again.
	 */
	@Test
	public void testStringValueList() {
		ValueList values = new ValueList();

		values.add(Value.of(1));
		values.add(Value.of(2));
		values.add(Value.of(3));
		values.add(Value.of(4));
		values.add(Value.of(5));

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(values);

		// convert back
		ValueList conv = HaleIO.getComplexValue(fragment, ValueList.class, null);

		assertEquals("List size does not match", values.size(), conv.size());

		for (int i = 0; i < values.size(); i++) {
			assertEquals(values.get(i), conv.get(i));
		}
	}

	/**
	 * Test if a simple list containing only {@link StringValue}s is the same
	 * when converted to DOM and back again.
	 */
	@Test
	public void testValueListValueList() {
		ValueList values1 = new ValueList();
		values1.add(Value.of(1));
		values1.add(Value.of(2));

		ValueList values2 = new ValueList();
		values2.add(Value.of("a"));
		values2.add(Value.of("b"));
		values2.add(Value.of("c"));

		ValueList values = new ValueList();
		values.add(new ComplexValue(values1));
		values.add(new ComplexValue(values2));

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(values);

		// convert back
		ValueList conv = HaleIO.getComplexValue(fragment, ValueList.class, null);

		assertEquals("List size does not match", 2, conv.size());

		assertEquals(values, conv);
	}

}
