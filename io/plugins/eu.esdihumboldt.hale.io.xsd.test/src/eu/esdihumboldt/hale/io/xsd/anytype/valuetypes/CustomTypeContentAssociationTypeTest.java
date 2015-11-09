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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContent;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentAssociation;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentMode;

/**
 * Test {@link CustomTypeContentAssociation} serialization.
 * 
 * @author Simon Templer
 */
public class CustomTypeContentAssociationTypeTest {

	/**
	 * Test converting to DOM and back again.
	 */
	@Test
	public void testBothWays() {
		QName name1 = new QName("typespace", "some type");
		QName name2 = new QName("a1");
		QName name3 = new QName("b1");
		List<QName> property = new ArrayList<>();
		property.add(name1);
		property.add(name2);
		property.add(name3);

		QName elem1 = new QName("mimimi");
		QName elem2 = new QName("some namespace", "some name");
		List<QName> elements = new ArrayList<QName>();
		elements.add(elem1);
		elements.add(elem2);
		CustomTypeContent content = new CustomTypeContent(CustomTypeContentMode.elements, elements);

		CustomTypeContentAssociation assoc = new CustomTypeContentAssociation(property, content);

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(assoc);

//		System.out.println(XmlUtil.serialize(fragment));

		// convert back
		CustomTypeContentAssociation conv = HaleIO.getComplexValue(fragment,
				CustomTypeContentAssociation.class, null);

		assertNotNull(conv.getProperty());
		assertEquals(3, conv.getProperty().size());
		assertEquals(name1, conv.getProperty().get(0));
		assertEquals(name2, conv.getProperty().get(1));
		assertEquals(name3, conv.getProperty().get(2));

		assertEquals("Mode does not match", CustomTypeContentMode.elements, conv.getConfig()
				.getMode());
		assertEquals(2, conv.getConfig().getElements().size());
		assertEquals(elem1, conv.getConfig().getElements().get(0));
		assertEquals(elem2, conv.getConfig().getElements().get(1));
	}
}
