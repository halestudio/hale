package eu.esdihumboldt.hale.io.codelist.xml.content;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.esdihumboldt.hale.common.core.io.tester.AbstractXmlTester;
import eu.esdihumboldt.hale.io.codelist.xml.content.XmlCodeListContentTypeTester;

/**
 * Testing class for {@link XmlCodeListContentTypeTester}
 * @author Patrick Lieb
 */
public class XmlCodeListContentTypeTesterTest {

	/**
	 * Test loading xml code list
	 */
	@Test
	public void testTrueXmlCodeList(){
		AbstractXmlTester tester = new XmlCodeListContentTypeTester();
		
		assertTrue(tester.matchesContentType(getClass().getResourceAsStream("/data/AdministrativeHierarchyLevel.xml")));
	}
	
	/**
	 * Test loading normal xml file
	 */
	@Test
	public void testFalseXmlCodeList(){
		AbstractXmlTester tester = new XmlCodeListContentTypeTester();
		
		assertFalse(tester.matchesContentType(getClass().getResourceAsStream("/data/testxml.xml")));
	}
}
