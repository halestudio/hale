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

package eu.esdihumboldt.hale.ui.common.definition.internal.editors;

import org.eclipse.swt.widgets.Composite;

import eu.esdihumboldt.hale.ui.common.internal.Messages;

/**
 * Editor for integer attributes
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class IntegerAttributeEditor extends StringValidatingAttributeEditor<Integer> {

	/**
	 * @see StringValidatingAttributeEditor#StringValidatingAttributeEditor(Composite)
	 */
	public IntegerAttributeEditor(Composite parent) {
		super(parent);
	}

	/**
	 * @see StringValidatingAttributeEditor#validate(String)
	 */
	@Override
	protected String validate(String text) {
		if (text == null) {
			// allow empty value by default
			return null;
		}
		
		try {
			Integer.parseInt(text);
			return null;
		}
		catch (NumberFormatException e) {
			return Messages.IntegerAttributeEditor_0; //$NON-NLS-1$
		}
	}
	
	/**
	 * @see StringValidatingAttributeEditor#getValidToolTip()
	 */
	@Override
	protected String getValidToolTip() {
		return Messages.IntegerAttributeEditor_1; //$NON-NLS-1$
	}

	/**
	 * @see StringValidatingAttributeEditor#stringFromValue(Object)
	 */
	@Override
	protected String stringFromValue(Integer value) {
		return value.toString();
	}

	/**
	 * @see StringValidatingAttributeEditor#valueFromString(String)
	 */
	@Override
	protected Integer valueFromString(String text) {
		return Integer.parseInt(text);
	}

	/**
	 * @see StringValidatingAttributeEditor#emptyStringIsNull()
	 */
	@Override
	protected boolean emptyStringIsNull() {
		return true;
	}

}
