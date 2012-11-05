/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.io.project.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectWriter;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Archive Project Writer Configuration Page for additional web resources
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectWriterConfigurationPage extends
		AbstractConfigurationPage<ArchiveProjectWriter, IOWizard<ArchiveProjectWriter>> {

	private Button button;

	private static final String WEB_RESOURCES = "webresources";

	/**
	 * 
	 */
	public ArchiveProjectWriterConfigurationPage() {
		super("archiveWriter");

		setTitle("Web resources");
		setDescription("Pack web resources to the archive");
	}

	/**
	 * @see AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// do nothing
	}

	/**
	 * @see AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// do nothing
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));

		button = new Button(page, SWT.CHECK);
		button.setSelection(false); // default
		button.setText("Retrieve also all web resources");

		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(ArchiveProjectWriter provider) {
		provider.setParameter(WEB_RESOURCES, String.valueOf(button.getSelection()));
		return true;
	}

}
