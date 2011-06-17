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

package eu.esdihumboldt.hale.io.gml.content;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import eu.esdihumboldt.hale.io.xml.content.AbstractXmlTester;

/**
 * TODO Type description
 * 
 * @author Patrick
 */
public class GmlContentTypeTester extends AbstractXmlTester {

	/**
	 * @see eu.esdihumboldt.hale.core.io.ContentTypeTester#matchesContentType(java.io.InputStream)
	 */
	@Override
	protected boolean testReader(XMLStreamReader reader)
			throws XMLStreamException {
		boolean rightNS = false;
		boolean rightName = false;
		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLStreamConstants.START_ELEMENT) {
				rightName = reader.getLocalName().equals("FeatureCollection");
				for (int count = reader.getNamespaceCount() -1; count >= 0; count--) {
					// TODO GML 3.2
					rightNS = rightNS
							|| reader.getNamespaceURI(count).equals(
									"http://www.opengis.net/gml")
							|| reader.getNamespaceURI(count).equals(
									"http://www.opengis.net/gml/3.2");
				}
			}
			return rightNS && rightName;

		}
		return false;
	}

}
