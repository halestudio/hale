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

package eu.esdihumboldt.hale.io.xsd.anytype.valuetypes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContent;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentMode;

/**
 * Test {@link CustomTypeContent} serialization.
 * 
 * @author Simon Templer
 */
public class CustomTypeContentTypeTest {

	/**
	 * Test with mode simple.
	 */
	@Test
	public void testSimple() {
		CustomTypeContent content = new CustomTypeContent(CustomTypeContentMode.simple,
				new ArrayList<QName>());

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(content);

		// convert back
		CustomTypeContent conv = HaleIO.getComplexValue(fragment, CustomTypeContent.class, null);

		assertEquals("Mode does not match", CustomTypeContentMode.simple, conv.getMode());
		assertEquals(0, conv.getElements().size());
	}

	/**
	 * Test with mode simple.
	 */
	@Test
	public void testElements() {
		QName name1 = new QName("mimimi");
		QName name2 = new QName("some namespace", "some name");

		List<QName> elements = new ArrayList<QName>();
		elements.add(name1);
		elements.add(name2);
		CustomTypeContent content = new CustomTypeContent(CustomTypeContentMode.elements, elements);

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(content);

//		System.out.println(XmlUtil.serialize(fragment));

		// convert back
		CustomTypeContent conv = HaleIO.getComplexValue(fragment, CustomTypeContent.class, null);

		assertEquals("Mode does not match", CustomTypeContentMode.elements, conv.getMode());
		assertEquals(2, conv.getElements().size());
		assertEquals(name1, conv.getElements().get(0));
		assertEquals(name2, conv.getElements().get(1));
	}

}
