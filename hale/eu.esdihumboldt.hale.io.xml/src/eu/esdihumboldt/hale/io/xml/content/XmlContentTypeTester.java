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

package eu.esdihumboldt.hale.io.xml.content;


import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import eu.esdihumboldt.hale.core.io.ContentTypeTester;

/**
 * TODO Type description
 * 
 * @author Patrick Lieb
 */
public class XmlContentTypeTester extends AbstractXmlTester implements ContentTypeTester {

	/**
	 * @param reader
	 * @return 
	 * @throws XMLStreamException
	 */
	@Override
	protected boolean testReader(XMLStreamReader reader) throws XMLStreamException {
		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLStreamConstants.START_ELEMENT) {

				return true;
			}
		}
		
		return false;
	}

}