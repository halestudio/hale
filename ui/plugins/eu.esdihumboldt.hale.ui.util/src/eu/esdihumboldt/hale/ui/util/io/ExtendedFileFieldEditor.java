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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * File field editor that allows setting the file dialog style and filter names
 * or {@link IContentType}s
 */
public class ExtendedFileFieldEditor extends FileFieldEditor {

	private String[] extensions;

	private String[] names;

	private int style;

	/**
	 * Create a file field editor
	 * 
	 * @param style the file dialog style
	 */
	protected ExtendedFileFieldEditor(int style) {
		super();

		this.style = style;
	}

	/**
	 * Create a file field editor
	 * 
	 * @param name the preference name
	 * @param labelText the label text
	 * @param enforceAbsolute <code>true</code> if the file path must be
	 *            absolute, and <code>false</code> otherwise
	 * @param parent the parent composite
	 * @param style the file dialog style
	 * 
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, Composite)
	 */
	public ExtendedFileFieldEditor(String name, String labelText, boolean enforceAbsolute,
			Composite parent, int style) {
		super(name, labelText, enforceAbsolute, parent);

		this.style = style;
	}

	/**
	 * Create a file field editor
	 * 
	 * @param name the preference name
	 * @param labelText the label text
	 * @param enforceAbsolute <code>true</code> if the file path must be
	 *            absolute, and <code>false</code> otherwise
	 * @param validationStrategy the validation strategy
	 * @param parent the parent composite
	 * @param style the file dialog style
	 * 
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, int,
	 *      Composite)
	 */
	public ExtendedFileFieldEditor(String name, String labelText, boolean enforceAbsolute,
			int validationStrategy, Composite parent, int style) {
		super(name, labelText, enforceAbsolute, validationStrategy, parent);

		this.style = style;
	}

	/**
	 * Create a file field editor
	 * 
	 * @param name the preference name
	 * @param labelText the label text
	 * @param parent the parent composite
	 * @param style the file dialog style
	 * 
	 * @see FileFieldEditor#FileFieldEditor(String, String, Composite)
	 */
	public ExtendedFileFieldEditor(String name, String labelText, Composite parent, int style) {
		super(name, labelText, parent);

		this.style = style;
	}

	/**
	 * @see FileFieldEditor#changePressed()
	 */
	@Override
	protected String changePressed() {
		File f = new File(getTextControl().getText());
		if (!f.exists()) {
			f = null;
		}
		File d = getFile(f);
		if (d == null) {
			return null;
		}

		return d.getAbsolutePath();
	}

	/**
	 * Helper to open the file chooser dialog.
	 * 
	 * @param startingDirectory the directory to open the dialog on.
	 * @return File The File the user selected or <code>null</code> if they do
	 *         not.
	 */
	protected File getFile(File startingDirectory) {
		FileDialog dialog = new FileDialog(getShell(), style);
		if (startingDirectory != null) {
			dialog.setFileName(startingDirectory.getPath());
		}
		if (extensions != null) {
			dialog.setFilterExtensions(extensions);
		}
		if (names != null) {
			dialog.setFilterNames(names);
		}
		String file = dialog.open();
		if (file != null) {
			file = file.trim();
			if (file.length() > 0) {
				return new File(file);
			}
		}

		return null;
	}

	/**
	 * Sets this file field editor's file extension filter.
	 * 
	 * @param extensions a list of file extension, or <code>null</code> to set
	 *            the filter to the system's default value
	 */
	@Override
	public void setFileExtensions(String[] extensions) {
		this.extensions = extensions;
	}

	/**
	 * Sets this file field editor's file extension filter names.
	 * 
	 * @param names a list of filter names, must correspond with the extensions
	 *            set using {@link #setFileExtensions(String[])}
	 */
	public void setFilterNames(String[] names) {
		this.names = names;
	}

	/**
	 * Set the content types, this is an alternative to using
	 * {@link #setFileExtensions(String[])} and
	 * {@link #setFilterNames(String[])}
	 * 
	 * @param types the content types
	 */
	public void setContentTypes(Set<IContentType> types) {
		List<String> filters = new ArrayList<String>();
		List<String> extensions = new ArrayList<String>();
		for (IContentType type : types) {
			String[] exts = type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
			if (exts != null && exts.length > 0) {
				StringBuffer filterName = new StringBuffer();
				StringBuffer filterExtension = new StringBuffer();
				filterName.append(type.getName());
				filterName.append(" (");
				boolean first = true;
				for (String ext : exts) {
					if (first) {
						first = false;
					}
					else {
						filterName.append(", ");
						filterExtension.append(";");
					}
					filterName.append("*.");
					filterName.append(ext);
					filterExtension.append("*.");
					filterExtension.append(ext);
				}
				filterName.append(")");

				filters.add(filterName.toString());
				extensions.add(filterExtension.toString());
			}
		}

		if ((style & SWT.OPEN) != 0) {
			// insert filter for all supported files
			if (extensions.size() > 1) {
				StringBuffer supportedExtensions = new StringBuffer();
				boolean first = true;
				for (String ext : extensions) {
					if (first) {
						first = false;
					}
					else {
						supportedExtensions.append(";");
					}
					supportedExtensions.append(ext);
				}

				filters.add(0, "All supported files");
				extensions.add(0, supportedExtensions.toString());
			}
		}

		filters.add("All files");
		extensions.add("*.*");

		setFileExtensions(extensions.toArray(new String[extensions.size()]));
		setFilterNames(filters.toArray(new String[filters.size()]));
	}

}
