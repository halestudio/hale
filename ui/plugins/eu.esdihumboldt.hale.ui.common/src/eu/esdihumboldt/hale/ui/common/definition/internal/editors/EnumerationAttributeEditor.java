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

import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eu.esdihumboldt.hale.ui.common.definition.AbstractAttributeEditor;
import eu.esdihumboldt.hale.ui.common.definition.AttributeEditor;

/**
 * Editor for enumeration attributes
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class EnumerationAttributeEditor extends AbstractAttributeEditor<String> {

	private final ComboViewer viewer;

	private boolean otherValuesAllowed;
	private Object value;

	/**
	 * Create an enumeration attribute editor
	 * 
	 * @param parent the parent composite
	 * @param allowedValues the collection of allowed values
	 * @param otherValuesAllowed states if other values shall be allowed
	 */
	public EnumerationAttributeEditor(Composite parent, Collection<?> allowedValues, boolean otherValuesAllowed) {
		super();
		
		this.otherValuesAllowed = otherValuesAllowed;
		
		viewer = new ComboViewer(parent, ((otherValuesAllowed)?(SWT.NONE):(SWT.READ_ONLY)) | SWT.BORDER);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider());
		
		viewer.setInput(allowedValues);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object newValue = getValue();
				if ((value == null && newValue != null) || (value != null && !value.equals(newValue))) {
					fireValueChanged(VALUE, value, newValue);
					value = newValue;
				}
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
		return viewer.getControl();
	}

	/**
	 * @see AttributeEditor#getValue()
	 */
	@Override
	public String getValue() {
		if (otherValuesAllowed) {
			return viewer.getCombo().getText();
		}
		else {
			ISelection selection = viewer.getSelection();
			if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
				return null;
			}
			else {
				return ((IStructuredSelection) selection).getFirstElement().toString();
			}
		}
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
		if (otherValuesAllowed) {
			viewer.getCombo().setText(value);
		}
		else {
			viewer.setSelection(new StructuredSelection(value), true);
		}
	}

	/**
	 * @see AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return true;
	}

}
