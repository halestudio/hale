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

package eu.esdihumboldt.hale.io.jdbc.spatialite;

import java.io.File;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;

/**
 * TODO Type description
 * 
 * @author stefano
 */
public class SpatiaLiteJdbcIOSupplier extends FileIOSupplier {

	private URI jdbcUri;

	/**
	 * @param file
	 */
	public SpatiaLiteJdbcIOSupplier(File file) {
		super(file);
		if (file != null) {
			jdbcUri = new SpatiaLiteURIBuilder().createJdbcUri(null, file.getAbsolutePath());
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

	public String getDatabaseFilePath() {
		return SpatiaLiteURIBuilder.getDatabase(getLocation());
	}

}
