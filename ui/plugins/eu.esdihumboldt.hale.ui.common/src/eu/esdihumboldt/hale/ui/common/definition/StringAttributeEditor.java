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

package eu.esdihumboldt.hale.ui.common.definition;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;


/**
 * Simple attribute editor for string values.
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StringAttributeEditor extends AbstractAttributeEditor<String> {
	
	private final Text text;

	/**
	 * Create a string attribute editor.
	 * @param parent the parent composite
	 */
	public StringAttributeEditor(Composite parent) {
		text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				fireValueChanged(VALUE, null, text.getText());
			}

		});
	}
	
	/**
	 * @see AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		return getValue();
	}

	/**
	 * @see AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return text;
	}

	/**
	 * @see AttributeEditor#getValue()
	 */
	@Override
	public String getValue() {
		return text.getText();
	}

	/**
	 * @see AttributeEditor#setAsText(String)
	 */
	@Override
	public void setAsText(String text) {
		setValue(text);
	}

	/**
	 * @see AttributeEditor#setValue(Object)
	 */
	@Override
	public void setValue(String value) {
		if (value == null) {
			// can't handle null value
			text.setText("");
		}
		text.setText(value);
	}

	/**
	 * @see AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return true;
	}

}
