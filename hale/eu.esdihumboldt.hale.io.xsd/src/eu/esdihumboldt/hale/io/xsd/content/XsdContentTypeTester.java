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

import javax.xml.XMLConstants;

import eu.esdihumboldt.hale.core.io.tester.XmlRootElementTester;

/**
 * Content type tester for XML schemas. Matches the root element name.
 * 
 * @author Patrick Lieb
 * 
 */
public class XsdContentTypeTester extends XmlRootElementTester {
	
	/**
	 * Default constructor
	 */
	public XsdContentTypeTester() {
		super("schema", XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

}
