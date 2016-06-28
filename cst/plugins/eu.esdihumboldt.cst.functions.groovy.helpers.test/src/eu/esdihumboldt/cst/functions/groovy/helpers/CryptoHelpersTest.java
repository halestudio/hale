/*
 * Copyright (c) 2016 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.groovy.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * Tests for cryptographic helper functions.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("javadoc")
public class CryptoHelpersTest {

	@Test
	public void testSha256_String() {
		String hash = CryptoHelpers._sha256("test");
		assertNotNull(hash);
		assertEquals("9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08", hash);
	}

}
