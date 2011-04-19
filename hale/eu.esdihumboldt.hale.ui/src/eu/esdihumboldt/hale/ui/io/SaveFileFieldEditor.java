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

package eu.esdihumboldt.hale.ui.io;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

/**
 * File field editor that opens a save instead of an open dialog
 */
public class SaveFileFieldEditor extends FileFieldEditor {
	
	private String[] extensions;

	/**
	 * Default constructor
	 */
	protected SaveFileFieldEditor() {
		super();
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, Composite)
	 */
	public SaveFileFieldEditor(String name, String labelText,
			boolean enforceAbsolute, Composite parent) {
		super(name, labelText, enforceAbsolute, parent);
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, boolean, int, Composite)
	 */
	public SaveFileFieldEditor(String name, String labelText,
			boolean enforceAbsolute, int validationStrategy, Composite parent) {
		super(name, labelText, enforceAbsolute, validationStrategy, parent);
	}

	/**
	 * @see FileFieldEditor#FileFieldEditor(String, String, Composite)
	 */
	public SaveFileFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
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
     * @param startingDirectory the directory to open the dialog on.
     * @return File The File the user selected or <code>null</code> if they
     * do not.
     */
    protected File getFile(File startingDirectory) {
        FileDialog dialog = new FileDialog(getShell(), SWT.SAVE | SWT.SHEET);
        if (startingDirectory != null) {
			dialog.setFileName(startingDirectory.getPath());
		}
        if (extensions != null) {
			dialog.setFilterExtensions(extensions);
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
     * @param extensions a list of file extension, or <code>null</code> 
     * to set the filter to the system's default value
     */
    @Override
	public void setFileExtensions(String[] extensions) {
        this.extensions = extensions;
    }

}
