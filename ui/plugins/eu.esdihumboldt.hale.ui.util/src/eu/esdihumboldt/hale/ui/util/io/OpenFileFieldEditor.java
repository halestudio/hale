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

package eu.esdihumboldt.hale.ui.util.io;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * File field editor that opens a save instead of an open dialog
 */
public class OpenFileFieldEditor extends ExtendedFileFieldEditor {

	private static final int OPEN_STYLE = SWT.OPEN | SWT.SHEET;

	/**
	 * Default constructor
	 */
	protected OpenFileFieldEditor() {
		super(OPEN_STYLE);
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, Composite)
	 */
	public OpenFileFieldEditor(String name, String labelText, boolean enforceAbsolute,
			Composite parent) {
		super(name, labelText, enforceAbsolute, parent, OPEN_STYLE);
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, int,
	 *      Composite)
	 */
	public OpenFileFieldEditor(String name, String labelText, boolean enforceAbsolute,
			int validationStrategy, Composite parent) {
		super(name, labelText, enforceAbsolute, validationStrategy, parent, OPEN_STYLE);
	}

	/**
	 * @see ExtendedFileFieldEditor#ExtendedFileFieldEditor(String, String,
	 *      boolean, int, Composite, int )
	 */
	public OpenFileFieldEditor(String name, String labelText, boolean enforceAbsolute,
			int validationStrategy, Composite parent, int style) {
		super(name, labelText, enforceAbsolute, validationStrategy, parent, style);
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, Composite)
	 */
	public OpenFileFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent, OPEN_STYLE);
	}

}
