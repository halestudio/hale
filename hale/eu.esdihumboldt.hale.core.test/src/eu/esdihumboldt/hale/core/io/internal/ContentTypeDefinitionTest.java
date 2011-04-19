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

package eu.esdihumboldt.hale.core.io.internal;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

/**
 * Test cases for {@link ContentTypeDefinition}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ContentTypeDefinitionTest {

	/**
	 * Test loading a content type definition
	 * 
	 * @throws Exception if an error occurs
	 */
	@Test
	public void testLoad() throws Exception {
		ContentTypeDefinition ctd = ContentTypeDefinition.load(getClass().getResourceAsStream("example.xml"));
		
		assertEquals("Test", ctd.getIdentifier());
		
		assertEquals("Type for test purposes", ctd.getDefaultName());
		
		assertNotNull(ctd.getNames());
		assertEquals(1, ctd.getNames().size());
		assertEquals(Locale.GERMAN, ctd.getNames().keySet().iterator().next());
		assertEquals("Test-Typ", ctd.getNames().values().iterator().next());
		
		assertNotNull(ctd.getFileExtensions());
		assertEquals(1, ctd.getFileExtensions().size());
		assertEquals("xml", ctd.getFileExtensions().iterator().next());
		
		assertNull(ctd.getTesterClassName());
	}
	
}
