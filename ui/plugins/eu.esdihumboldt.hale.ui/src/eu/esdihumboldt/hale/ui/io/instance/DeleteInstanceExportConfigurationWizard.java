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

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Wizard for deletion of saved export configurations
 * 
 * @author Patrick Lieb
 */
public class DeleteInstanceExportConfigurationWizard extends InstanceExportWizard {

	private List<IOConfiguration> selectedConfigs;

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		// only the deletion page is needed
		addPage(new DeleteInstanceExportConfigurationPage());
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		return super.canFinish();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.instance.InstanceExportWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// remove all selected export configurations from the project
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		ps.removeExportConfigurations(selectedConfigs);
		return true;
	}

	/**
	 * Set the export configurations to be deleted by the wizard
	 * 
	 * @param selConfigs the selected export configurations
	 */
	public void setSelection(List<Object> selConfigs) {
		selectedConfigs = new ArrayList<IOConfiguration>();
		for (Object conf : selConfigs)
			if (conf instanceof IOConfiguration)
				selectedConfigs.add((IOConfiguration) conf);
	}
}
