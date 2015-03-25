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

package eu.esdihumboldt.hale.io.wfs.ui.getfeature;

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
 * Wizard for determining the transaction URL from WFS capabilities.
 * 
 * @author Simon Templer
 */
public class WFSGetFeatureWizard extends ConfigurationWizard<WFSGetFeatureConfig> {

	/**
	 * @see ConfigurationWizard#ConfigurationWizard(Object)
	 */
	public WFSGetFeatureWizard(WFSGetFeatureConfig configuration) {
		super(configuration);
		setWindowTitle("Determine from Capabilities");
	}

	@Override
	protected boolean validate(WFSGetFeatureConfig configuration) {
		return configuration.getGetFeatureUri() != null && configuration.getVersion() != null;
	}

	@Override
	public void addPages() {
		super.addPages();

		/**
		 * Page for specifying the WFS capabilities URL.
		 */
		AbstractWFSCapabilitiesPage<WFSGetFeatureConfig> capPage = new AbstractWFSCapabilitiesPage<WFSGetFeatureConfig>(
				this) {

			@Override
			protected boolean updateConfiguration(WFSGetFeatureConfig configuration,
					URL capabilitiesUrl, WFSCapabilities capabilities) {
				if (capabilities != null && capabilities.getGetFeatureOp() != null) {
					WFSOperation op = capabilities.getGetFeatureOp();

					configuration.setGetFeatureUri(URI.create(op.getHttpGetUrl()));
					configuration.setVersion(capabilities.getVersion());
					return true;
				}
				// TODO show error message ?
				return false;
			}
		};
		addPage(capPage);

		addPage(new AbstractFeatureTypesPage<WFSGetFeatureConfig>(this, capPage,
				"Specify the feature types to request") {

			@Override
			protected void updateState(Set<QName> selected) {
				// at least one type must be specified
				setPageComplete(!selected.isEmpty());
			}

			@Override
			protected boolean updateConfiguration(WFSGetFeatureConfig configuration,
					Set<QName> selected) {
				configuration.getTypeNames().addAll(selected);
				return true;
			}

		});
	}

}
