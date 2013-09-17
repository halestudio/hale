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

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectWriter;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.io.IOWizard;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Archive project writer configuration page for additional project resources.
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectWriterConfigurationPage extends
		AbstractConfigurationPage<ArchiveProjectWriter, IOWizard<ArchiveProjectWriter>> {

	private Button includeWebResources;

	private Button excludeDataFiles;

	/**
	 * Default Constuctor
	 */
	public ArchiveProjectWriterConfigurationPage() {
		super("archiveWriter");

		setTitle("Additonal Export Options");
		setDescription("Adapt options to include or exclude resources to the archive");
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

		excludeDataFiles = new Button(page, SWT.CHECK);
		excludeDataFiles.setSelection(false); // default
		excludeDataFiles.setText("Exclude source data");

		includeWebResources = new Button(page, SWT.CHECK);
		includeWebResources.setSelection(false); // default
		includeWebResources.setText("Include web resources (http)");

		setPageComplete(true);
	}

	@Override
	public boolean updateConfiguration(ArchiveProjectWriter provider) {
		provider.setParameter(ArchiveProjectWriter.EXLUDE_DATA_FILES,
				Value.of(excludeDataFiles.getSelection()));
		provider.setParameter(ArchiveProjectWriter.INCLUDE_WEB_RESOURCES,
				Value.of(includeWebResources.getSelection()));
		return true;
	}

}
