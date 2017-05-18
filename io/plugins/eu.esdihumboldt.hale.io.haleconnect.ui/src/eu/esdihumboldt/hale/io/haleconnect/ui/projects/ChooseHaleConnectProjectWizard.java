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

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Wizard for choosing a hale connect project.
 * 
 * @author Florian Esser
 */
public class ChooseHaleConnectProjectWizard extends ConfigurationWizard<HaleConnectProjectConfig> {

	/**
	 * Create a new wizard
	 * 
	 * @param configuration the configuration object
	 */
	public ChooseHaleConnectProjectWizard(HaleConnectProjectConfig configuration) {
		super(configuration);
		setWindowTitle("Load project from hale connect");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard#validate(java.lang.Object)
	 */
	@Override
	protected boolean validate(HaleConnectProjectConfig configuration) {
		return configuration.getProjectId() != null && !configuration.getProjectId().isEmpty();
	}

	@Override
	public void addPages() {
		super.addPages();

		ChooseHaleConnectProjectWizardPage selectProjectPage = new ChooseHaleConnectProjectWizardPage(
				this);
		addPage(selectProjectPage);
	}

	/**
	 * Open a {@link ChooseHaleConnectProjectWizard}
	 * 
	 * @return the selected project or null if none was selected
	 */
	public static HaleConnectProjectConfig openSelectProject() {
		HaleConnectProjectConfig config = new HaleConnectProjectConfig();
		ChooseHaleConnectProjectWizard wizard = new ChooseHaleConnectProjectWizard(config);
		HaleWizardDialog dialog = new HaleWizardDialog(Display.getCurrent().getActiveShell(),
				wizard);
		dialog.setMinimumPageSize(800, 300);

		if (dialog.open() == WizardDialog.OK) {
			return wizard.getConfiguration();
		}

		return null;
	}

}
