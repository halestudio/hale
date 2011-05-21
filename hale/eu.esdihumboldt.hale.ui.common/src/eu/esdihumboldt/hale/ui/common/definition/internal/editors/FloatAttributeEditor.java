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
 * Editor for float values
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class FloatAttributeEditor extends StringValidatingAttributeEditor<Float> {

	/**
	 * @see StringValidatingAttributeEditor#StringValidatingAttributeEditor(Composite)
	 */
	public FloatAttributeEditor(Composite parent) {
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
			Float.parseFloat(text);
			return null;
		}
		catch (NumberFormatException e) {
			return Messages.FloatAttributeEditor_0; //$NON-NLS-1$
		}
	}
	
	/**
	 * @see StringValidatingAttributeEditor#getValidToolTip()
	 */
	@Override
	protected String getValidToolTip() {
		return Messages.FloatAttributeEditor_1; //$NON-NLS-1$
	}

	/**
	 * @see StringValidatingAttributeEditor#stringFromValue(Object)
	 */
	@Override
	protected String stringFromValue(Float value) {
		return value.toString();
	}

	/**
	 * @see StringValidatingAttributeEditor#valueFromString(String)
	 */
	@Override
	protected Float valueFromString(String text) {
		return Float.parseFloat(text);
	}

	/**
	 * @see StringValidatingAttributeEditor#emptyStringIsNull()
	 */
	@Override
	protected boolean emptyStringIsNull() {
		return true;
	}

}
