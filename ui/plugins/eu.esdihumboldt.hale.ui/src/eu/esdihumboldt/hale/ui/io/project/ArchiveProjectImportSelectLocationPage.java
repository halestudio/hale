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

package eu.esdihumboldt.hale.ui.io.project;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectImport;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;

/**
 * Selection page for the directory to chose where the project archive should be
 * imported
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectImportSelectLocationPage extends
		AbstractConfigurationPage<ArchiveProjectImport, ArchiveProjectImportWizard> {

	private String location = "";

	/**
	 * Default Constructor
	 */
	protected ArchiveProjectImportSelectLocationPage() {
		super("archiveProjectImportConfiguration");
		setTitle("Choose directory for archive extraction");
		setDescription("The project archive will be extracted and loaded in the specific directory.");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#enable()
	 */
	@Override
	public void enable() {
		// not needed here

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage#disable()
	 */
	@Override
	public void disable() {
		// not needed here

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.io.IOWizardPage#updateConfiguration(eu.esdihumboldt.hale.common.core.io.IOProvider)
	 */
	@Override
	public boolean updateConfiguration(ArchiveProjectImport provider) {
		provider.setParameter("PARAM_IMPORT_LOCATION", Value.of(location));
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.HaleWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		final DirectoryFieldEditor dialog = new DirectoryFieldEditor("Import location",
				"Select directory", page);
		setPageComplete(false);
		dialog.getTextControl(page).addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				location = dialog.getStringValue();
				if (!location.isEmpty())
					setPageComplete(true);
			}
		});
	}

	/**
	 * @return the location of the directory
	 */
	protected String getLocation() {
		return location;
	}
}
