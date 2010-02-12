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

package eu.esdihumboldt.hale.rcp.utils.definition.internal.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.rcp.utils.definition.AttributeEditor;

/**
 * Attribute editor for boolean values
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class BooleanAttributeEditor implements AttributeEditor<Boolean> {

	private final Button button;
	
	/**
	 * Create a boolean attribute editor
	 * 
	 * @param parent the parent composite
	 */
	public BooleanAttributeEditor(Composite parent) {
		super();
		
		//XXX combo with true/false a better solution?
		button =  new Button(parent, SWT.CHECK);
	}

	/**
	 * @see AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		return getValue().toString();
	}

	/**
	 * @see AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return button;
	}

	/**
	 * @see AttributeEditor#getValue()
	 */
	@Override
	public Boolean getValue() {
		return button.getSelection();
	}

	/**
	 * @see AttributeEditor#setAsText(String)
	 */
	@Override
	public void setAsText(String text) {
		setValue(Boolean.parseBoolean(text));
	}

	/**
	 * @see AttributeEditor#setValue(Object)
	 */
	@Override
	public void setValue(Boolean value) {
		button.setSelection(value);
	}
	
}
