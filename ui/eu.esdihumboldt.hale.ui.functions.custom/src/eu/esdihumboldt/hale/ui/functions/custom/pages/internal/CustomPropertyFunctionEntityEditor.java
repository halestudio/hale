/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.functions.custom.pages.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity;
import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.editors.AbstractCompositeEditor;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
public class CustomPropertyFunctionEntityEditor extends
		AbstractCompositeEditor<DefaultCustomPropertyFunctionEntity> {

	private Text nameText;
	private Spinner minSpinner;
	private Spinner maxSpinner;
	private Button eagerSelect;
	private Button unboundedSelect;
	private BindingOrTypeEditor bindingOrType;

	/**
	 * @see AbstractCompositeEditor#AbstractCompositeEditor(Composite)
	 */
	public CustomPropertyFunctionEntityEditor(Composite parent) {
		super(parent);
	}

	@Override
	protected void createControls(Composite page) {
		GridLayoutFactory.swtDefaults().numColumns(4).equalWidth(true).applyTo(page);

		GridDataFactory labelStyle = GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER)
				.grab(false, false);
		GridDataFactory fieldStyle = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false);
		GridDataFactory longFieldStyle = fieldStyle.copy().span(3, 1);
		GridDataFactory checkStyle = fieldStyle.copy().span(2, 1);

		// name
		Label nameLabel = new Label(page, SWT.NONE);
		nameLabel.setText("Name:");
		labelStyle.applyTo(nameLabel);
		nameText = new Text(page, SWT.SINGLE | SWT.BORDER);
		longFieldStyle.applyTo(nameText);

		// binding / type
		Label typeLabel = new Label(page, SWT.NONE);
		typeLabel.setText("Type:");
		labelStyle.applyTo(typeLabel);
		bindingOrType = new BindingOrTypeEditor(page, SchemaSpaceID.SOURCE);
		longFieldStyle.applyTo(bindingOrType.getControl());

		// min
		Label minLabel = new Label(page, SWT.NONE);
		minLabel.setText("Min:");
		labelStyle.applyTo(minLabel);
		minSpinner = new Spinner(page, SWT.BORDER);
		minSpinner.setValues(1, 0, 100, 0, 1, 10);
		fieldStyle.applyTo(minSpinner);

		// max
		Label maxLabel = new Label(page, SWT.NONE);
		maxLabel.setText("Max:");
		labelStyle.applyTo(maxLabel);
		maxSpinner = new Spinner(page, SWT.BORDER);
		maxSpinner.setValues(1, 1, 100, 0, 1, 10);
		fieldStyle.applyTo(maxSpinner);

		// eager
		eagerSelect = new Button(page, SWT.CHECK);
		eagerSelect.setText("eager");
		checkStyle.applyTo(eagerSelect);

		// unbounded
		unboundedSelect = new Button(page, SWT.CHECK);
		unboundedSelect.setText("unbounded");
		checkStyle.applyTo(unboundedSelect);
	}

	@Override
	public void setValue(DefaultCustomPropertyFunctionEntity value) {
		if (value == null) {
			nameText.setText("");
			minSpinner.setSelection(1);
			maxSpinner.setSelection(1);
			unboundedSelect.setSelection(false);
			eagerSelect.setSelection(false);
			return;
		}

		nameText.setText((value.getName() == null) ? ("") : (value.getName()));
		minSpinner.setSelection(value.getMinOccurrence());
		if (value.getMaxOccurrence() == ParameterDefinition.UNBOUNDED) {
			maxSpinner.setSelection(value.getMinOccurrence());
			unboundedSelect.setSelection(true);
		}
		else {
			maxSpinner.setSelection(value.getMaxOccurrence());
			unboundedSelect.setSelection(false);
		}
		eagerSelect.setSelection(value.isEager());

		// binding type
		BindingOrType bot = new BindingOrType();

		bot.setType(value.getBindingType());
		bot.setBinding(value.getBindingClass());
		bot.setUseBinding(value.getBindingType() == null);

		bindingOrType.setValue(bot);
	}

	@Override
	public DefaultCustomPropertyFunctionEntity getValue() {
		DefaultCustomPropertyFunctionEntity result = new DefaultCustomPropertyFunctionEntity();

		result.setName(nameText.getText());
		result.setEager(eagerSelect.getSelection());
		result.setMaxOccurrence((unboundedSelect.getSelection()) ? (ParameterDefinition.UNBOUNDED)
				: (maxSpinner.getSelection()));
		result.setMinOccurrence(minSpinner.getSelection());

		// binding type
		BindingOrType bot = bindingOrType.getValue();
		if (bot.isUseBinding()) {
			result.setBindingType(null);
			result.setBindingClass(bot.getBinding());
		}
		else {
			result.setBindingClass(null);
			result.setBindingType(bot.getType());
		}
		result.setBindingClass(String.class);

		return result;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return true;
	}

}
