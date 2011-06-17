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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.esdihumboldt.hale.core.io.tester.AbstractXmlTester;

/**
 * TODO Type description
 * @author Patrick Lieb
 */
public class XmlContentTypeTesterTest {
	
	/**
	 * Test loading a XML-file
	 */
	@Test
	public void testTrueXml(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertTrue(tester.matchesContentType(getClass().getResourceAsStream("/data/testxml.xml")));
	
	}
	
	/**
	 * Test loading a GML-file
	 */
	@Test
	public void testTrueGml(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertTrue(tester.matchesContentType(getClass().getResourceAsStream("/data/wfs_va_sample.gml")));

	}
	
	/**
	 * Test loading a XSD-file
	 */
	@Test
	public void testTrueXsd(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertTrue(tester.matchesContentType(getClass().getResourceAsStream("/data/wfs_va.xsd")));

	}
	
	/**
	 * Test loading a text-file
	 */
	@Test
	public void testFalseTxt(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertFalse(tester.matchesContentType(getClass().getResourceAsStream("/data/testtext.txt")));
	}

}
