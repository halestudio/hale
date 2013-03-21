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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.ExportProvider;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Save instance export configuration in the {@link Project}
 * 
 * @author Patrick Lieb
 */
public class SaveConfigurationInstanceExportWizard extends InstanceExportWizard {

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#execute(eu.esdihumboldt.hale.common.core.io.IOProvider,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(IOProvider provider, IOReporter defaultReporter) {

		IOConfiguration configuration = new IOConfiguration();
		// store the (export) configuration of the provider in the new IO
		// configuration
		configuration.setActionId(getActionId());
		configuration.setProviderId(getProvider().getContentType().getId());
		provider.storeConfiguration(configuration.getProviderConfiguration());

		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		// target is not set here and also not needed for the configuration
		configuration.getProviderConfiguration().remove(ExportProvider.PARAM_TARGET);
		// add the new configuration to the export configurations of the project
		List<IOConfiguration> configList = new ArrayList<IOConfiguration>();
		configList.add(configuration);
		ps.addExportConfigurations(configList);

		// no provider is executed so we return the default reporter
		defaultReporter.setSuccess(true);
		return defaultReporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.ExportWizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		// add page to save current export configuration
		addPage(new SaveConfigurationInstanceExportPage());
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		IWizardPage nextPage = super.getNextPage(page);
		if (nextPage != null) {
			if (!(nextPage.getName().equals("export.selTarget"))) {
				return nextPage;
			}
			else {
				// no selection of the export file is desired, so we skip the
				// export select target page
				getSelectTargetPage().setPageComplete(true);
				return super.getNextPage(getSelectTargetPage());
			}
		}
		return nextPage;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.instance.InstanceExportWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// set a dummy instance collection to pass provider validation
		getProvider().setInstances(new InstanceCollection() {

			@Override
			public InstanceReference getReference(Instance instance) {
				return null;
			}

			@Override
			public Instance getInstance(InstanceReference reference) {
				return null;
			}

			@Override
			public int size() {
				return 0;
			}

			@Override
			public InstanceCollection select(Filter filter) {
				return null;
			}

			@Override
			public ResourceIterator<Instance> iterator() {
				return null;
			}

			@Override
			public boolean isEmpty() {
				// collection must not be empty to pass validation
				return false;
			}

			@Override
			public boolean hasSize() {
				return false;
			}
		});
		return super.performFinish();
	}
}
