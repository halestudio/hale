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
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectImport;
import eu.esdihumboldt.hale.ui.io.config.AbstractConfigurationPage;
import eu.esdihumboldt.hale.ui.util.io.KeyStrokeValidatingDirectoryFieldEditor;

/**
 * Selection page for the directory to chose where the project archive should be
 * imported
 * 
 * @author Patrick Lieb
 */
public class ArchiveProjectImportSelectLocationPage extends
		AbstractConfigurationPage<ArchiveProjectImport, ArchiveProjectImportWizard> {

	private final String location = "";

	private DirectoryFieldEditor directoryField;

	/**
	 * Default Constructor
	 */
	protected ArchiveProjectImportSelectLocationPage() {
		super("archiveProjectImportConfiguration");
		setTitle("Choose directory for archive extraction");
		setDescription("The project archive will be extracted and loaded in the specific directory.");

		setPageComplete(false);
	}

	@Override
	public void enable() {
		// not needed here

	}

	@Override
	public void disable() {
		// not needed here

	}

	@Override
	public boolean updateConfiguration(ArchiveProjectImport provider) {
		provider.setParameter("PARAM_IMPORT_LOCATION", Value.of(directoryField.getStringValue()));
		return true;
	}

	@Override
	protected void createContent(Composite page) {
		directoryField = new KeyStrokeValidatingDirectoryFieldEditor("Import location",
				"Select directory", page);
		directoryField.setEmptyStringAllowed(false);
		directoryField.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					updateState();
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					updateState();
				}
			}
		});
	}

	/**
	 * Update the page state.
	 */
	protected void updateState() {
		boolean valid = directoryField.isValid();

		if (!valid) {
			setErrorMessage(directoryField.getErrorMessage());
			setPageComplete(false);
		}
		else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}
}
