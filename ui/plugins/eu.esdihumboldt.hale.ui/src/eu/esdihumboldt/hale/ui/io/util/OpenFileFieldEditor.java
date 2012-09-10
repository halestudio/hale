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
	 * @see FileFieldEditor#FileFieldEditor(String, String, Composite)
	 */
	public OpenFileFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent, OPEN_STYLE);
	}

}
