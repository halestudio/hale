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

package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.io.File;
import java.io.InputStream;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.jdbc.JDBCSchemaReader;

/**
 * Reads a schema from a MSAccess DB. extended {@link JDBCSchemaReader}.
 * 
 * @author Arun
 */
public class MsAccessSchemaReader extends JDBCSchemaReader {

	/**
	 * Default Constructor
	 */
	public MsAccessSchemaReader() {
		setUseQuotes(false);
	}

	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {
		MsAccessJdbcIOSupplier inputSource = new MsAccessJdbcIOSupplier(
				new File(source.getLocation()));
		super.setSource(inputSource);
	}

}
