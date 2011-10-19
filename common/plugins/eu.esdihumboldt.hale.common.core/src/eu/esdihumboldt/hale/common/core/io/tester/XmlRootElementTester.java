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

package eu.esdihumboldt.hale.common.core.io.tester;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Content type tester that checks the root element of an XML file.
 * @author Simon Templer
 */
@Deprecated
public abstract class XmlRootElementTester extends AbstractXmlTester {

	private final String localName;
	
	private final String namespace;
	
	/**
	 * Create a content type tester that matches local name and/or namespace
	 * of the root element of an XML file
	 * @param localName the local name the root element should have to match
	 *   the associated content type, or <code>null</code>
	 * @param namespace the namespace the root element should have to match
	 *   the associated content type, or <code>null</code>
	 */
	public XmlRootElementTester(String localName, String namespace) {
		super();
		this.localName = localName;
		this.namespace = namespace;
	}

	/**
	 * @see AbstractXmlTester#testReader(XMLStreamReader)
	 */
	@Override
	protected boolean testReader(XMLStreamReader reader)
			throws XMLStreamException {
		while (reader.hasNext()) {
			int event = reader.next();
			if (event == XMLStreamConstants.START_ELEMENT) {
				// match local name
				if (localName != null && !localName.equals(reader.getLocalName())) {
					return false;
				}
				
				// match namespace
				if (namespace != null && !namespace.equals(reader.getNamespaceURI())) {
					return false;
				}
				
				return true;
			}
		
		}
		return false;
	}

}
