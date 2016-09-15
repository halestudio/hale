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

import java.text.MessageFormat;

import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.ui.io.instance.InstanceExportWizard;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Save instance export configuration in the {@link Project}
 * 
 * @author Patrick Lieb
 */
public class SaveConfigurationInstanceExportWizard extends InstanceExportWizard {

	private static final ALogger log = ALoggerFactory
			.getLogger(SaveConfigurationInstanceExportWizard.class);

	private String configurationName;

	/**
	 * @return the name of the export configuration
	 */
	public String getConfigurationName() {
		return configurationName;
	}

	/**
	 * @param configurationName the export configuration name to set
	 */
	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;
	}

	@Override
	public void addPages() {
		super.addPages();
		// add page to save current export configuration
		addPage(new SaveConfigurationInstanceExportPage());
	}

	@Override
	public boolean performFinish() {
		if (!applyConfiguration()) {
			return false;
		}

		IOConfiguration configuration = new IOConfiguration();
		// store the (export) configuration of the provider in the new IO
		// configuration
		configuration.setActionId(getActionId());
		configuration.setProviderId(getProviderFactory().getIdentifier());
		getProvider().storeConfiguration(configuration.getProviderConfiguration());

		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		// target is not set here and also not needed for the configuration
		configuration.getProviderConfiguration().remove(ExportProvider.PARAM_TARGET);
		// add the new configuration to the export configurations of the project
		ps.addExportConfiguration(configurationName, configuration);

		log.userInfo(
				MessageFormat.format("Created export configuration ''{0}''", configurationName));

		return true;
	}

	@Override
	protected boolean suppressTargetPage() {
		// target page is to be ignored
		return true;
	}

}
