/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.io.util;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * File field editor that opens a save instead of an open dialog
 * 
 * @since 2.2
 */
public class SaveFileFieldEditor extends ExtendedFileFieldEditor {

	private static final int SAVE_STYLE = SWT.SAVE | SWT.SHEET;

	/**
	 * Indicates whether the path must be absolute; <code>false</code> by
	 * default.
	 */
	private boolean enforceAbsolute = false;

	/**
	 * Default constructor
	 */
	protected SaveFileFieldEditor() {
		super(SAVE_STYLE);

		init();
	}

	private void init() {
		// change the editor error message
		setErrorMessage("Value must be a valid file name to save to");
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, Composite)
	 */
	public SaveFileFieldEditor(String name, String labelText, boolean enforceAbsolute,
			Composite parent) {
		super(name, labelText, enforceAbsolute, parent, SAVE_STYLE);

		this.enforceAbsolute = enforceAbsolute;

		init();
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, int,
	 *      Composite)
	 */
	public SaveFileFieldEditor(String name, String labelText, boolean enforceAbsolute,
			int validationStrategy, Composite parent) {
		super(name, labelText, enforceAbsolute, validationStrategy, parent, SAVE_STYLE);

		this.enforceAbsolute = enforceAbsolute;

		init();
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, Composite)
	 */
	public SaveFileFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent, SAVE_STYLE);

		init();
	}

	/**
	 * @see FileFieldEditor#checkState()
	 */
	@Override
	protected boolean checkState() {
		String msg = null;

		String path = getTextControl().getText();
		if (path != null) {
			path = path.trim();
		}
		else {
			path = "";//$NON-NLS-1$
		}
		if (path.length() == 0) {
			if (!isEmptyStringAllowed()) {
				msg = getErrorMessage();
			}
		}
		else {
			File file = new File(path);
			if (isValid(file)) {
				if (enforceAbsolute && !file.isAbsolute()) {
					msg = JFaceResources.getString("FileFieldEditor.errorMessage2");//$NON-NLS-1$
				}
			}
			else {
				msg = getErrorMessage();
			}
		}

		if (msg != null) { // error
			showErrorMessage(msg);
			return false;
		}

		// OK!
		clearErrorMessage();
		return true;
	}

	/**
	 * Checks if the given file is valid, may be overridden
	 * 
	 * @param file the file
	 * @return if the file is valid
	 */
	protected boolean isValid(File file) {
		if (file.isAbsolute()) {
			if (file.isDirectory()) {
				// file is a directory
				return false;
			}

			// some parent must be a directory
			File parent = file.getParentFile();
			while (parent != null) {
				if (parent.isDirectory()) {
					return true;
				}

				parent = parent.getParentFile();
			}

			return false;
		}
		else {
			// no validation for files that are not absolute
			return true;
		}
	}

}
