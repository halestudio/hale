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

package eu.esdihumboldt.hale.io.wfs.ui.describefeature;

import java.net.URI;
import java.net.URL;

import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSOperation;
import eu.esdihumboldt.hale.io.wfs.ui.capabilities.AbstractWFSCapabilitiesPage;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;

/**
 * Wizard for determining the transaction URL from WFS capabilities.
 * 
 * @author Simon Templer
 */
public class WFSDescribeFeatureWizard extends ConfigurationWizard<WFSDescribeFeatureConfig> {

	/**
	 * @see ConfigurationWizard#ConfigurationWizard(Object)
	 */
	public WFSDescribeFeatureWizard(WFSDescribeFeatureConfig configuration) {
		super(configuration);
		setWindowTitle("Determine from Capabilities");
	}

	@Override
	protected boolean validate(WFSDescribeFeatureConfig configuration) {
		return configuration.getDescribeFeatureUri() != null && configuration.getVersion() != null;
	}

	@Override
	public void addPages() {
		super.addPages();

		/**
		 * Page for specifying the WFS capabilities URL.
		 */
		addPage(new AbstractWFSCapabilitiesPage<WFSDescribeFeatureConfig>(this) {

			@Override
			protected boolean updateConfiguration(WFSDescribeFeatureConfig configuration,
					URL capabilitiesUrl, WFSCapabilities capabilities) {
				if (capabilities != null && capabilities.getDescribeFeatureOp() != null) {
					WFSOperation op = capabilities.getDescribeFeatureOp();

					configuration.setDescribeFeatureUri(URI.create(op.getHttpGetUrl()));
					configuration.setVersion(capabilities.getVersion());
					return true;
				}
				// TODO show error message ?
				return false;
			}
		});
	}

}
