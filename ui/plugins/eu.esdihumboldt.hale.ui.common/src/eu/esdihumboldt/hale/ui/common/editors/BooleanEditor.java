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

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.common.AttributeEditor;

/**
 * Attribute editor for boolean values
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class BooleanEditor extends AbstractAttributeEditor<Boolean> {

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
		return combo.getControl();
	}

	/**
	 * @see AttributeEditor#getValue()
	 */
	@Override
	public Boolean getValue() {
		return (Boolean) ((IStructuredSelection) combo.getSelection()).getFirstElement();
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
		combo.setSelection(new StructuredSelection(value));
	}

	/**
	 * @see AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValueType()
	 */
	@Override
	public String getValueType() {
		return ParameterValue.DEFAULT_TYPE;
	}
}
