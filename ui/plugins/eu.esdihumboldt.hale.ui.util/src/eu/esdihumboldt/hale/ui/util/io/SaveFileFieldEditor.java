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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

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
	 * Indicates whether specifying an URI is allowed
	 */
	private boolean allowUri = false;

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
		} else {
			File file = null;
			if (allowUri) {
				// check if string is an uri
				try {
					URI uri = new URI(path);

					// check if the URI references a file
					try {
						file = new File(uri);
						// is a file, just continue with normal validity check
					} catch (IllegalArgumentException e) {
						// no file
						return isValid(uri);
					}
				} catch (URISyntaxException e) {
					// ignore - no URI, try file
				}
			}

			if (file == null) {
				file = new File(path);
			}

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
	
	/**
	 * Checks if the given URI is valid, may be overridden
	 * 
	 * @param uri the URI
	 * @return if the URI is valid
	 */
	protected boolean isValid(URI uri) {
		// accept all well-formed URIs
		return true;
	}

	/**
	 * @return the allowUri
	 */
	public boolean isAllowUri() {
		return allowUri;
	}

	/**
	 * @param allowUri the allowUri to set
	 */
	public void setAllowUri(boolean allowUri) {
		this.allowUri = allowUri;
	}

}
