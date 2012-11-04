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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

	private Button webresources;

	private Button nodatafiles;

	private static final String WEB_RESOURCES = "webresources";

	private static final String NO_DATA_FILES = "nodatafiles";

	/**
	 * 
	 */
	public ArchiveProjectWriterConfigurationPage() {
		super("archiveWriter");

		setTitle("Additonal Export Options");
		setDescription("Choose resources which should be pack to the archive");
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

		nodatafiles = new Button(page, SWT.CHECK);
		nodatafiles.setSelection(false); // default
		nodatafiles.setText("Retrieve only project and alignment files");
		nodatafiles.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (nodatafiles.getSelection()) {
					webresources.setSelection(false);
					webresources.setEnabled(false);
				}
				else
					webresources.setEnabled(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// nothing
			}

		});

		webresources = new Button(page, SWT.CHECK);
		webresources.setSelection(false); // default
		webresources.setText("Retrieve also all web resources");

		setPageComplete(true);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(ArchiveProjectWriter provider) {
		provider.setParameter(NO_DATA_FILES, String.valueOf(nodatafiles.getSelection()));
		provider.setParameter(WEB_RESOURCES, String.valueOf(webresources.getSelection()));
		return true;
	}

}
