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


import org.junit.Before;
import org.junit.Test;

/**
 * TODO Type description
 * @author Patrick Lieb
 */
public class XmlContentTypeTesterTest {
	
	/**
	 * Checks if a XML-File could be identified correctly
	 */
	
//	@Before
//	void loadTester(){
//		XmlContentTypeTester tester = new XmlContentTypeTester();
//	}
	
	@Test
	public void testTrueXml(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertTrue(tester.matchesContentType(tester.getClass().getResourceAsStream("/data/testxml.xml")));

	}
	
	/**
	 * 
	 */
	@Test
	public void testTrueGml(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertTrue(tester.matchesContentType(tester.getClass().getResourceAsStream("/data/wfs_va_sample.gml")));

	}
	
	/**
	 * 
	 */
	@Test
	public void testTrueXsd(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertTrue(tester.matchesContentType(tester.getClass().getResourceAsStream("/data/wfs_va.xsd")));

	}
	
	/**
	 * Checks if a normal textfile is identified as false
	 */
	@Test
	public void testFalseTxt(){
		
		AbstractXmlTester tester = new XmlContentTypeTester();
		assertFalse(tester.matchesContentType(tester.getClass().getResourceAsStream("/data/testtext.txt")));
	}

}
