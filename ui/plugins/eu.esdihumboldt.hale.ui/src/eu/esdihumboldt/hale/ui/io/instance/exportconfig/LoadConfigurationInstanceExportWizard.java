/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.io.instance.exportconfig;

import org.eclipse.jface.wizard.IWizardPage;

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.instance.InstanceExportWizard;

/**
 * Configuration page for loading export configurations of instances
 * 
 * @author Patrick Lieb
 */
public class LoadConfigurationInstanceExportWizard extends InstanceExportWizard {

	private IOConfiguration configuration = new IOConfiguration();

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		// only the pages to select the configuration
		// which should be loaded and and the target file are needed
		addPage(new LoadConfigurationInstanceExportPage());
		addPage(new SelectTargetExportConfigurationPage());
	}

	/**
	 * Set the instance export configuration in the wizard
	 * 
	 * @param conf the export configuration
	 */
	public void setConfiguration(IOConfiguration conf) {
		configuration = conf;
	}

	/**
	 * @return the current configuration which should be loaded
	 */
	public IOConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		if (nextPage instanceof IOWizardPage<?, ?>) {
			String name = nextPage.getName();
			// before switching to the select target export configuration page,
			// the provider has to be updated
			if (name.equals("export.selTarget")) {
				InstanceWriter provider = getProvider();
				// prepare the provider by loading configuration from the
				// current io configuration
				if (provider != null)
					provider.loadConfiguration(configuration.getProviderConfiguration());
			}
		}
		return nextPage;
	}
}
