/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.jdbc.msaccess.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

/**
 * 
 * Test for MS Access database reader
 * 
 * @author Arun
 *
 */
public class MsAccessDataReaderTest {

	/**
	 * Testing connection and data reading from access database
	 */
	@Test
	public void testgetData() {

		MsAccessDataReaderTestSuit obj = new MsAccessDataReader();
		try {
			obj.createSourceTempFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String rs = obj.getFirstData();

		assertTrue(rs != null);
		assertTrue(rs.length() > 0);

		obj.deleteSourceTempFile();

	}

}
