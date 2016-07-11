/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.jdbc.msaccess;

import java.io.File;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;

/**
 * I/O supplier for Microsoft Access databases. Converts a file path to a JDBC URI and
 * viceversa.
 * 
 * @author Arun Varma
 */
public class MsAccessJdbcIOSupplier extends FileIOSupplier {

	private URI jdbcUri;
	
	public MsAccessJdbcIOSupplier(File file) {
		super(file);
		if (file != null) {
			jdbcUri = new MsAccessURIBuilder().createJdbcUri(null, file.toURI().getPath());
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier#getLocation()
	 */
	@Override
	public URI getLocation() {
		return jdbcUri;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier#getUsedLocation()
	 */
	@Override
	public URI getUsedLocation() {
		return jdbcUri;
	}

	/**
	 * @return the absolute path of the MS Access database file
	 */
	public String getDatabaseFilePath() {
		return MsAccessURIBuilder.getDatabase(getLocation());
	}

}
