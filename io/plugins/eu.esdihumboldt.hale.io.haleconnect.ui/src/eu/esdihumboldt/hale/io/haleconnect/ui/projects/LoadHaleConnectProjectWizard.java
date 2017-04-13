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

package eu.esdihumboldt.hale.io.haleconnect.ui.projects;

import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;

/**
 * Wizard for loading projects from hale connect.
 * 
 * @author Florian Esser
 */
public class LoadHaleConnectProjectWizard
		extends ConfigurationWizard<LoadHaleConnectProjectConfig> {

	/**
	 * Create a new wizard
	 * 
	 * @param configuration the configuration object
	 */
	public LoadHaleConnectProjectWizard(LoadHaleConnectProjectConfig configuration) {
		super(configuration);
		setWindowTitle("Load project from hale connect");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard#validate(java.lang.Object)
	 */
	@Override
	protected boolean validate(LoadHaleConnectProjectConfig configuration) {
		return configuration.getProjectId() != null && !configuration.getProjectId().isEmpty();
	}

	@Override
	public void addPages() {
		super.addPages();

		LoadHaleConnectProjectWizardPage selectProjectPage = new LoadHaleConnectProjectWizardPage(
				this);
		addPage(selectProjectPage);
	}

}
