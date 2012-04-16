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

package eu.esdihumboldt.util.orient.embedded;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import eu.esdihumboldt.util.orient.embedded.EmbeddedOrientDB;

/**
 * Embedded OrientDB server tests
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ServerTest {
	
	/**
	 * Test if a server instance is available
	 */
	@Ignore
	@Test
	public void testServerInstance() {
		assertNotNull(EmbeddedOrientDB.getServer());
	}

}
