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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

import eu.esdihumboldt.hale.io.gml.reader.internal.GmlInstanceCollection;
import eu.esdihumboldt.hale.io.xml.content.AbstractXmlTester;
import eu.esdihumboldt.hale.io.xml.content.XmlContentTypeTester;

/**
 * Tests for {@link }
 *
 * @author Patrick Lieb
 */
public class GmlContentTypeTesterTest {

	/**
	 * 
	 */
	@Test
	public void testTrueGml1(){
		GmlContentTypeTester tester = new GmlContentTypeTester();
		InputStream input = tester.getClass().getResourceAsStream("/data/wfs_va_sample.gml");
		assertTrue(tester.matchesContentType(input));

	}
	
	/**
	 * 
	 */
	@Test
	public void testTrueGml2(){
		GmlContentTypeTester tester = new GmlContentTypeTester();
		InputStream input = tester.getClass().getResourceAsStream("/data/KA_14168_EPSG25833.gml");
		assertTrue(tester.matchesContentType(input));
	}
	
	/**
	 * 
	 */
	@Test
	public void testFalseText(){
		GmlContentTypeTester tester = new GmlContentTypeTester();
		InputStream input = tester.getClass().getResourceAsStream("/data/testtext.txt");
		assertFalse(tester.matchesContentType(input));
	}
}
