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
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSOperation;
import eu.esdihumboldt.hale.io.wfs.ui.capabilities.AbstractWFSCapabilitiesPage;
import eu.esdihumboldt.hale.io.wfs.ui.types.AbstractFeatureTypesPage;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;

/**
 * Wizard for determining a DescribeFeatureType URL from WFS capabilities.
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
		AbstractWFSCapabilitiesPage<WFSDescribeFeatureConfig> capPage = new AbstractWFSCapabilitiesPage<WFSDescribeFeatureConfig>(
				this) {

			@Override
			protected boolean updateConfiguration(WFSDescribeFeatureConfig configuration,
					URL capabilitiesUrl, WFSCapabilities capabilities) {
				if (capabilities != null && capabilities.getDescribeFeatureOp() != null) {
					WFSOperation op = capabilities.getDescribeFeatureOp();

					configuration.setDescribeFeatureUri(URI.create(op.getHttpGetUrl()));
					configuration.setVersion(capabilities.getVersion());
					return true;
				}
				setErrorMessage("Invalid capabilities or WFS does not support DescribeFeatureType KVP");
				return false;
			}
		};
		addPage(capPage);

		addPage(new AbstractFeatureTypesPage<WFSDescribeFeatureConfig>(this, capPage,
				"Optionally request schema for specific feature types only") {

			@Override
			protected void updateState(Set<QName> selected) {
				// any selection allowed
				setPageComplete(true);
			}

			@Override
			protected boolean updateConfiguration(WFSDescribeFeatureConfig configuration,
					Set<QName> selected) {
				configuration.getTypeNames().clear();
				configuration.getTypeNames().addAll(selected);
				return true;
			}

		});
	}

}
