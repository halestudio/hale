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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContent;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentAssociation;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentConfiguration;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentMode;

/**
 * Test {@link CustomTypeContentConfiguration} serialization.
 * 
 * @author Simon Templer
 */
public class CustomTypeContentConfigurationTypeTest {

	/**
	 * Test converting to DOM and back again.
	 */
	@Test
	public void testBothWays() {
		QName name1 = new QName("http://www.opengis.net/om/2.0", "OM_ObservationType");
		QName name2 = new QName("http://www.opengis.net/om/2.0", "result");
		List<QName> property = new ArrayList<>();
		property.add(name1);
		property.add(name2);

		QName elem1 = new QName("http://www.opengis.net/swe/2.0", "Quantity");
		List<QName> elements = new ArrayList<QName>();
		elements.add(elem1);
		CustomTypeContent content = new CustomTypeContent(CustomTypeContentMode.elements, elements);
		CustomTypeContentAssociation assoc = new CustomTypeContentAssociation(property, content);

		List<CustomTypeContentAssociation> associations = new ArrayList<>();
		associations.add(assoc);
		CustomTypeContentConfiguration config = new CustomTypeContentConfiguration(associations);

		// convert to DOM
		Element fragment = HaleIO.getComplexElement(config);

//		System.out.println(XmlUtil.serialize(fragment));

		// convert back
		CustomTypeContentConfiguration conv = HaleIO.getComplexValue(fragment,
				CustomTypeContentConfiguration.class, null);

		assertEquals(1, conv.getAssociations().size());

		List<QName> cproperty = conv.getAssociations().get(0).getProperty();
		assertNotNull(cproperty);
		assertEquals(2, cproperty.size());
		assertEquals(name1, cproperty.get(0));
		assertEquals(name2, cproperty.get(1));

		CustomTypeContent ccontent = conv.getAssociations().get(0).getConfig();
		assertEquals("Mode does not match", CustomTypeContentMode.elements, ccontent.getMode());
		assertEquals(1, ccontent.getElements().size());
		assertEquals(elem1, ccontent.getElements().get(0));
	}

	/**
	 * Test loading from XML.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoad() throws Exception {
		QName name1 = new QName("http://www.opengis.net/om/2.0", "OM_ObservationType");
		QName name2 = new QName("http://www.opengis.net/om/2.0", "result");
		QName elem1 = new QName("http://www.opengis.net/swe/2.0", "Quantity");

		// load as DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream inputStream = getClass().getResourceAsStream("configSample.xml");
		Element root;
		try {
			root = builder.parse(inputStream).getDocumentElement();
		} finally {
			inputStream.close();
		}

		// convert back
		CustomTypeContentConfiguration conv = HaleIO.getComplexValue(root,
				CustomTypeContentConfiguration.class, null);

		assertEquals(1, conv.getAssociations().size());

		List<QName> cproperty = conv.getAssociations().get(0).getProperty();
		assertNotNull(cproperty);
		assertEquals(2, cproperty.size());
		assertEquals(name1, cproperty.get(0));
		assertEquals(name2, cproperty.get(1));

		CustomTypeContent ccontent = conv.getAssociations().get(0).getConfig();
		assertEquals("Mode does not match", CustomTypeContentMode.elements, ccontent.getMode());
		assertEquals(1, ccontent.getElements().size());
		assertEquals(elem1, ccontent.getElements().get(0));
	}
}
