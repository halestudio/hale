/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.tools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.esdihumboldt.commons.tools.ConfigurationManager;

/**
 * An Junittest for ConfigurationManager
 * 
 * @author Anna Pitaev
 * @partner 04 / Logica
 * @version $Id$
 */
public class ConfigurationManagerTest {

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.commons.tools.ConfigurationManager#getComponentProperty(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetComponentProperty() {
		String stackSize = ConfigurationManager
				.getComponentProperty("composedPropertyStackSize");
		assertEquals("4", stackSize);
	}

	/**
	 * Test method for
	 * {@link eu.esdihumboldt.commons.tools.ConfigurationManager#getSystemProperty(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetSystemProperty() {
		String log4jproperties = ConfigurationManager
				.getSystemProperty("log4jproperties");
		assertEquals("log4j.xml", log4jproperties);
	}

}
