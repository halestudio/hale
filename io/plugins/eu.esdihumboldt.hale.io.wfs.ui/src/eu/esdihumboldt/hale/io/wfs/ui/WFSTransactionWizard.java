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

package eu.esdihumboldt.hale.io.wfs.ui;

import java.net.URI;

import eu.esdihumboldt.hale.io.wfs.capabilities.WFSCapabilities;
import eu.esdihumboldt.hale.io.wfs.capabilities.WFSOperation;
import eu.esdihumboldt.hale.io.wfs.ui.capabilities.AbstractWFSCapabilitiesPage;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;

/**
 * Wizard for determining the transaction URL from WFS capabilities.
 * 
 * @author Simon Templer
 */
public class WFSTransactionWizard extends ConfigurationWizard<WFSTransactionConfig> {

	/**
	 * @see ConfigurationWizard#ConfigurationWizard(Object)
	 */
	public WFSTransactionWizard(WFSTransactionConfig configuration) {
		super(configuration);
		setWindowTitle("Determine from Capabilities");
	}

	@Override
	protected boolean validate(WFSTransactionConfig configuration) {
		return configuration.getTransactionUri() != null;
		// && configuration.getVersion() != null;
	}

	@Override
	public void addPages() {
		super.addPages();

		/**
		 * Page for specifying the WFS capabilities URL.
		 */
		addPage(new AbstractWFSCapabilitiesPage<WFSTransactionConfig>(this) {

			@Override
			protected boolean updateConfiguration(WFSTransactionConfig configuration,
					String capabilitiesUrl, WFSCapabilities capabilities) {
				if (capabilities != null && capabilities.getTransactionOp() != null) {
					WFSOperation op = capabilities.getTransactionOp();

					configuration.setTransactionUri(URI.create(op.getHttpPostUrl()));
					configuration.setVersion(capabilities.getVersion());
					return true;
				}
				// TODO show error message ?
				return false;
			}
		});
	}

}
