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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;

/**
 * Attribute editor for integers.
 * 
 * @author Simon Templer
 */
public class IntegerEditor extends AbstractAttributeEditor<Integer> {

	private Integer value;
	private final Spinner spinner;

	/**
	 * Create a boolean attribute editor
	 * 
	 * @param parent the parent composite
	 * @param max the maximum value
	 * @param min the minimum value
	 * @param increment the increment step size
	 * @param pageIncrement the page increment step size
	 */
	public IntegerEditor(Composite parent, int max, int min, int increment, int pageIncrement) {
		super();

		spinner = new Spinner(parent, SWT.BORDER);
		spinner.setIncrement(increment);
		spinner.setPageIncrement(pageIncrement);
		spinner.setDigits(0);
		spinner.setMinimum(min);
		spinner.setMaximum(max);

		spinner.setSelection(min);
		value = min;

		spinner.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				int newValue = spinner.getSelection();
				fireValueChanged(VALUE, value, newValue);
				value = newValue;
			}
		});

	}

	@Override
	public String getAsText() {
		return getValue().toString();
	}

	@Override
	public Control getControl() {
		return spinner;
	}

	@Override
	public Integer getValue() {
		return spinner.getSelection();
	}

	@Override
	public void setAsText(String text) {
		setValue(Integer.parseInt(text));
	}

	@Override
	public void setValue(Integer value) {
		spinner.setSelection(value);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String getValueType() {
		return ParameterValue.DEFAULT_TYPE;
	}
}
