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

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Save instance export configuration in project file
 * 
 * @author Patrick Lieb
 */
public class SaveInstanceExportWizard extends InstanceExportWizard {

	private final String param_validator = "validator";
	private final String valiator_not_set = "not set";

	/**
	 * @see eu.esdihumboldt.hale.ui.io.instance.InstanceExportWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		Project project = (Project) ps.getProjectInfo();
		IOConfiguration ioc = new IOConfiguration();
		ioc.setActionId(getActionId());
		// XXX get correct target file (with extension)
		ioc.getProviderConfiguration().put(ImportProvider.PARAM_SOURCE,
				getSelectTargetPage().getTargetFileName());
		ioc.getProviderConfiguration().put(IOProvider.PARAM_CONTENT_TYPE,
				getContentType().getName());
		if (getValidatorFactory() != null)
			ioc.getProviderConfiguration().put(param_validator,
					getValidatorFactory().getIdentifier());
		else
			ioc.getProviderConfiguration().put(param_validator, valiator_not_set);
		project.getExportConfigurations().add(ioc);
		// set login data for jdbc if available
		return true;
	}
}
