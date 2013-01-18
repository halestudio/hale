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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * TODO Type description
 * 
 * @author Patrick
 */
public class SaveInstanceExportNamePage extends
		AbstractConfigurationPage<InstanceWriter, SaveInstanceExportWizard> {

	private Text name;

	private final String param_configurationName = "configurationName";

	/**
	 * 
	 */
	public SaveInstanceExportNamePage() {
		super("Export Namepage");

		setTitle("Set name for the export configuration");
		setDescription("########Platzhalter###########");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(InstanceWriter provider) {
		if (name.getText().isEmpty())
			return false;
		provider.setParameter(param_configurationName, name.getText());
		return true;

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		// user
		Label labelUser = new Label(page, SWT.NONE);
		labelUser.setText("Configuration name:");
		labelUser.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).create());

		name = new Text(page, SWT.BORDER | SWT.SINGLE);
		name.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).create());
		setPageComplete(true);
	}
}
