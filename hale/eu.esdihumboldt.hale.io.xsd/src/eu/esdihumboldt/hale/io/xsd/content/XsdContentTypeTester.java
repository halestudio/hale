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

package eu.esdihumboldt.hale.io.xsd.content;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import eu.esdihumboldt.hale.core.io.tester.AbstractXmlTester;

import javax.xml.XMLConstants;

/**
 * Contenttype-Tester class for XSD-files
 * 
 * @author Patrick Lieb
 * 
 */
public class XsdContentTypeTester extends AbstractXmlTester {

	@Override
	protected boolean testReader(XMLStreamReader reader)
			throws XMLStreamException {

		boolean rightNS = false;
		boolean rightName = false;
		
		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLStreamConstants.START_ELEMENT) {
				rightName = reader.getLocalName().equals("schema");
				System.out.println(reader.getLocalName());
				if (reader.getNamespaceURI() == null)
					return false;
					rightNS = reader.getNamespaceURI().equals(
									XMLConstants.W3C_XML_SCHEMA_NS_URI);
			}
			return rightNS && rightName;
		}
		return false;
	}
}
