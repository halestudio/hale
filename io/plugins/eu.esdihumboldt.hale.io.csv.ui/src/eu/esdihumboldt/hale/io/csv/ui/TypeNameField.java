/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.io.csv.ui;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * The Class specifying the TextField for the Typename
 * 
 * @author Kevin Mais
 */
public class TypeNameField extends StringFieldEditor {

	Composite _parent;

	/**
	 * Parameter that signals when a test has changed
	 */
	public static final String TXT_CHNGD = "text_has_changed";

	/**
	 * @param name the name intern
	 * @param labelText the label to be set in the StringFieldEditor
	 * @param parent the composite for the StringFieldEditor to be added in
	 */
	public TypeNameField(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		_parent = parent;
	}

	/**
	 * @see org.eclipse.jface.preference.StringFieldEditor#checkState()
	 */
	@Override
	protected boolean doCheckState() {
		boolean containsIllegalChar;

		setErrorMessage("You have not entered a valid Name");

		Text txtField = getTextControl(_parent);
		String txt = txtField.getText();

		containsIllegalChar = txt.contains("/") || txt.contains(":") || txt.contains(".");

		return !(containsIllegalChar);
	}

	/**
	 * @see org.eclipse.jface.preference.StringFieldEditor#valueChanged()
	 */
	@Override
	protected void valueChanged() {
		super.valueChanged();
		fireValueChanged(TXT_CHNGD, "", getStringValue());
	}

}
