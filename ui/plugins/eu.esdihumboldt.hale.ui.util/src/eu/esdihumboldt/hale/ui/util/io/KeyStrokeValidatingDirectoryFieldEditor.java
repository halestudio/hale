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

package eu.esdihumboldt.hale.ui.util.io;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;

/**
 * Directory field editor that uses validation on key stroke strategy.
 * 
 * @author Simon Templer
 */
public class KeyStrokeValidatingDirectoryFieldEditor extends DirectoryFieldEditor {

	/**
	 * Creates a directory field editor.
	 * 
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent the parent of the field editor's control
	 */
	public KeyStrokeValidatingDirectoryFieldEditor(String name, String labelText, Composite parent) {
		super();

		init(name, labelText);
		setErrorMessage(JFaceResources.getString("DirectoryFieldEditor.errorMessage"));//$NON-NLS-1$
		setChangeButtonText(JFaceResources.getString("openBrowse"));//$NON-NLS-1$
		setValidateStrategy(VALIDATE_ON_KEY_STROKE);
		createControl(parent);
	}

}
