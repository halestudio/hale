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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.io.project.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.project.impl.DefaultProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.impl.ZipProjectWriter;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.IOWizardPage;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Configuration page for the {@link ZipProjectWriter}
 * 
 * @author Simon Templer
 */
public class ZipProjectWriterConfigurationPage extends
		AbstractConfigurationPage<DefaultProjectWriter, IOWizard<DefaultProjectWriter>> {

	private Button checkSeparateFiles;

	/**
	 * Default constructor
	 */
	public ZipProjectWriterConfigurationPage() {
		super("zipWriter");

		setTitle("Project file");
		setDescription("Project file settings");
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
	 * @see IOWizardPage#updateConfiguration(IOProvider)
	 */
	@Override
	public boolean updateConfiguration(DefaultProjectWriter provider) {
		provider.setUseSeparateFiles(checkSeparateFiles.getSelection());
		return true;
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(new GridLayout(1, false));

		checkSeparateFiles = new Button(page, SWT.CHECK);
		checkSeparateFiles.setSelection(false); // default
		checkSeparateFiles
				.setText("Place alignment next to the project file instead of inside the project archive");

		setPageComplete(true);
	}

}
