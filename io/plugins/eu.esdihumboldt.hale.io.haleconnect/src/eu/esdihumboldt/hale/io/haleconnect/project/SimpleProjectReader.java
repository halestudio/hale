/*
 * Copyright (c) 2019 wetransform GmbH
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
import java.io.InputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUrnBuilder;
import eu.esdihumboldt.hale.io.haleconnect.Owner;

/**
 * Project reader for hale connect that does not require a special input
 * supplier.
 * 
 * @author Simon Templer
 */
public class SimpleProjectReader extends HaleConnectProjectReader {

	private Owner owner;
	private String projectId;

	@Override
	public void setSource(LocatableInputSupplier<? extends InputStream> source) {

		if (projectId == null) {
			// only do this initialization once (archive project reader
			// internally changes the source as well)

			// extract information from source URL
			URI loc = source.getLocation();
			if (loc == null) {
				throw new IllegalStateException("Source location must be provided");
			}
			owner = HaleConnectUrnBuilder.extractProjectOwner(loc);
			projectId = HaleConnectUrnBuilder.extractProjectId(loc);
		}

		super.setSource(source);
	}

	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		// now that the service should be available, build the required source
		HaleConnectService hcs = getServiceProvider().getService(HaleConnectService.class);

		// TODO login if applicable? (based on reader parameters)
		// XXX for now only support public projects

		LocatableInputSupplier<? extends InputStream> hcSource;
		try {
			hcSource = hcs.loadProject(owner, projectId);
		} catch (HaleConnectException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		super.setSource(hcSource);

		return super.execute(progress, reporter);
	}

}
