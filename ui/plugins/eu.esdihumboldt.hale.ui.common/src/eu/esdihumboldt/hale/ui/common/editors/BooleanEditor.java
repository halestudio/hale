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

package eu.esdihumboldt.hale.ui.common.editors;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.ui.common.Editor;

/**
 * Attribute editor for boolean values
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class BooleanEditor extends AbstractEditor<Boolean> {

	private Boolean value;
	private final ComboViewer combo;

	/**
	 * Create a boolean attribute editor
	 * 
	 * @param parent the parent composite
	 */
	public BooleanEditor(Composite parent) {
		super();

		combo = new ComboViewer(parent, SWT.READ_ONLY);
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setLabelProvider(new LabelProvider());
		combo.setInput(new Object[] { Boolean.TRUE, Boolean.FALSE });

		// default selection
		combo.setSelection(new StructuredSelection(Boolean.FALSE));
		value = Boolean.FALSE;
		combo.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Boolean newValue = (Boolean) ((IStructuredSelection) event.getSelection())
						.getFirstElement();
				fireValueChanged(VALUE, value, newValue);
				value = newValue;
			}
		});
	}

	/**
	 * @see Editor#getAsText()
	 */
	@Override
	public String getAsText() {
		return getValue().toString();
	}

	/**
	 * @see Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return combo.getControl();
	}

	/**
	 * @see Editor#getValue()
	 */
	@Override
	public Boolean getValue() {
		return (Boolean) ((IStructuredSelection) combo.getSelection()).getFirstElement();
	}

	/**
	 * @see Editor#setAsText(String)
	 */
	@Override
	public void setAsText(String text) {
		setValue(Boolean.parseBoolean(text));
	}

	/**
	 * @see Editor#setValue(Object)
	 */
	@Override
	public void setValue(Boolean value) {
		combo.setSelection(new StructuredSelection(value));
	}

	/**
	 * @see Editor#isValid()
	 */
	@Override
	public boolean isValid() {
		return true;
	}

}
