/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.io.haleconnect;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;

/**
 * I/O supplier for projects imported from hale connect
 * 
 * @author Florian Esser
 */
public class HaleConnectInputSupplier extends DefaultInputSupplier {

	private final File projectArchive;
	private final HaleConnectProjectInfo projectInfo;

	/**
	 * Create the input supplier based on the
	 * 
	 * @param location the location URI
	 * @param projectArchive The downloaded project archive
	 * @param projectInfo Details on the hale connect project
	 */
	public HaleConnectInputSupplier(URI location, File projectArchive,
			HaleConnectProjectInfo projectInfo) {
		super(location);

		this.projectArchive = projectArchive;
		this.projectInfo = projectInfo;
	}

	@Override
	public InputStream getInput() throws IOException {
		return new BufferedInputStream(new FileInputStream(projectArchive));
	}

	/**
	 * @return details on the hale connect project
	 */
	public HaleConnectProjectInfo getProjectInfo() {
		return this.projectInfo;
	}

	/**
	 * @return the hale connect project archive
	 */
	public File getProjectArchive() {
		return this.getProjectArchive();
	}

}
