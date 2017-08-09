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

package eu.esdihumboldt.hale.io.haleconnect.project;

import java.io.IOException;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectReader;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectInputSupplier;

/**
 * Project reader that reads a project archive and adds additional information
 * on the hale connect project to the project properties, if available.
 * 
 * @author Florian Esser
 */
public class HaleConnectProjectReader extends ArchiveProjectReader {

	/**
	 * The project property name for the hale connect project ID
	 */
	public static final String HALECONNECT_URN_PROPERTY = "haleconnect.urn";

	/**
	 * The project property name for the last modified timestamp on hale connect
	 */
	public static final String HALECONNECT_LAST_MODIFIED_PROPERTY = "haleconnect.lastModified";

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectReader#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		IOReport result = super.execute(progress, reporter);

		if (getSource() instanceof HaleConnectInputSupplier) {
			HaleConnectInputSupplier source = (HaleConnectInputSupplier) getSource();
			getProject().getProperties().put(HALECONNECT_LAST_MODIFIED_PROPERTY,
					Value.of(source.getLastModified()));
			getProject().getProperties().put(HALECONNECT_URN_PROPERTY,
					Value.of(source.getLocation()));
		}

		return result;
	}
}
