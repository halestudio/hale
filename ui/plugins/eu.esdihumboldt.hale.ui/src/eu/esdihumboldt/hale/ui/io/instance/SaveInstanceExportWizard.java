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

package eu.esdihumboldt.hale.ui.io.instance;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.ui.io.ExportSelectTargetPage;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Save instance export configuration in project file
 * 
 * @author Patrick Lieb
 */
public class SaveInstanceExportWizard extends InstanceExportWizard {

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#execute(eu.esdihumboldt.hale.common.core.io.IOProvider,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(IOProvider provider, IOReporter defaultReporter) {

		IOConfiguration configuration = new IOConfiguration();
		configuration.setActionId(getActionId());
		configuration.setProviderId(getProviderFactory().getIdentifier());
		provider.storeConfiguration(configuration.getProviderConfiguration());

		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		Project project = (Project) ps.getProjectInfo();
		// target not set and not needed for the configuration
		configuration.getProviderConfiguration().remove(ExportProvider.PARAM_TARGET);
		project.getExportConfigurations().add(configuration);

		defaultReporter.setSuccess(true);
		return defaultReporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		addPage(new SaveInstanceExportNamePage());
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (!(super.getNextPage(page) instanceof ExportSelectTargetPage<?, ?>))
			return super.getNextPage(page);
		else {
			getSelectTargetPage().setPageComplete(true);
			return super.getNextPage(getSelectTargetPage());
		}
	}
}
