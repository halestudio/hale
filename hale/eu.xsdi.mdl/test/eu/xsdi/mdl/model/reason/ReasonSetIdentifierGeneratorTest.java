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

package eu.xsdi.mdl.model.reason;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import eu.xsdi.mdl.model.reason.ReasonSetIdentifierGenerator;

/**
 * TODO Add Type comment
 * 
 * @author thorsten
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @since 2.0.0.M2
 */
public class ReasonSetIdentifierGeneratorTest {

	/**
	 * Test method for {@link eu.xsdi.mdl.model.reason.ReasonSetIdentifierGenerator#next()}.
	 */
	@Test
	public void testNext() {
		ReasonSetIdentifierGenerator rsig = new ReasonSetIdentifierGenerator();
		for (int i = 0; i < 100; i++) {
			String result = rsig.next();
			System.out.println(i + ": " + result);
			if (i == 0) {
				Assert.assertEquals("a", result);
			}
			if (i == 26) {
				Assert.assertEquals("aa", result);
			}
			if (i == 51) {
				Assert.assertEquals("az", result);
			}
			if (i == 52) {
				Assert.assertEquals("ba", result);
			}
		}
	}

}
