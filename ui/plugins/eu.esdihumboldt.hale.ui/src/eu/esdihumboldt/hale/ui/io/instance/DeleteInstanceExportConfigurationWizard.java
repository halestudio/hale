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
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Wizard for deletion of saved export configurations
 * 
 * @author Patrick Lieb
 */
public class DeleteInstanceExportConfigurationWizard extends InstanceExportWizard {

	private List<Object> selection = new ArrayList<Object>();

	/**
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
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
		for (Object config : selection) {
			getExportConfigurations().remove(config);
		}
		return true;
	}

	/**
	 * @return the exportConfigs
	 */
	public List<IOConfiguration> getExportConfigurations() {
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
		Project p = (Project) ps.getProjectInfo();
		return p.getExportConfigurations();
	}

	/**
	 * @param selection the selected export configurations
	 */
	public void setSelection(List<Object> selection) {
		this.selection = selection;
	}
}
