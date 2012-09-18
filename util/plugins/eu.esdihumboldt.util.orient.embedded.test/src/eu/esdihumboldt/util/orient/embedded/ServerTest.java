/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
