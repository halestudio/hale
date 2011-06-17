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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

import eu.esdihumboldt.hale.io.xsd.content.XsdContentTypeTester;

/**
 * Test class loading xsd, gml and xml files
 * @author Patrick Lieb
 */
public class XsdContentTypTesterTest {

	/**
	 * Test loading XSD-file
	 */
	@Test
	public void testXsd1True(){
		XsdContentTypeTester tester = new XsdContentTypeTester();
		InputStream input = getClass().getResourceAsStream("/testdata/ctttest/shiporder.xsd");
		assertTrue(tester.matchesContentType(input));
	}
	
	/**
	 * Test loading XSD-file
	 */
	@Test
	public void testXsd2True(){
		XsdContentTypeTester tester = new XsdContentTypeTester();
		InputStream input = getClass().getResourceAsStream("/testdata/ctttest/wfs_va.xsd");
		assertTrue(tester.matchesContentType(input));
	}
	
	/**
	 * Test loading GML-file
	 */
	@Test
	public void testGmlFalse(){
		XsdContentTypeTester tester = new XsdContentTypeTester();
		InputStream input = getClass().getResourceAsStream("/testdata/ctttest/ERM_Watercourse_FME.gml");
		assertFalse(tester.matchesContentType(input));
	}
	
	/**
	 * Test loading XML-file
	 */
	@Test
	public void testXmlFalse(){
		XsdContentTypeTester tester = new XsdContentTypeTester();
		InputStream input = getClass().getResourceAsStream("/testdata/ctttest/watercourse_va.xml");
		assertFalse(tester.matchesContentType(input));
	}
}
