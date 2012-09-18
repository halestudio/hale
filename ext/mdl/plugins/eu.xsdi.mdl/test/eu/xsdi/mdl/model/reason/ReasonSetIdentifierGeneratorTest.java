/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
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
