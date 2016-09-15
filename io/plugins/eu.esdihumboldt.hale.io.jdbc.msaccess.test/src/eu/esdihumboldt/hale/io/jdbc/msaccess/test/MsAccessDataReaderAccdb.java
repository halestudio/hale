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

/**
 * Concrete class for MsAccessDataReaderTestSuit for .accdb extension
 * 
 * @author Arun
 */
public class MsAccessDataReaderAccdb extends MsAccessDataReaderTestSuit {

	/**
	 * 
	 */
	public MsAccessDataReaderAccdb() {
		SOURCE_DB_NAME = "transform";

		SOURCE_DB_EXT = ".accdb";

		SOURCE_DB_PATH = "data/" + SOURCE_DB_NAME + SOURCE_DB_EXT;

		USER_NAME = null;

		PASSWORD = null;
	}
}
