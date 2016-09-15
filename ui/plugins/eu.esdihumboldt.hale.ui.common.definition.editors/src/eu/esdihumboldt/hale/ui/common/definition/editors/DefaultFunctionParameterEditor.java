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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.common.definition.editors;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.core.parameter.Validator;

/**
 * A default function parameter editor using binding and validator.
 * 
 * @author Kai Schwierczek
 */
public class DefaultFunctionParameterEditor extends AbstractBindingValidatingEditor<Object> {

	private final Validator validator;
	private final Text text;
	private final Composite composite;
	private final ControlDecoration decoration;

	/**
	 * Creates an editor for the given binding and validator.
	 * 
	 * @param parent the parent composite
	 * @param binding the function parameter's binding
	 * @param validator the optional validator, may be <code>null</code>
	 */
	public DefaultFunctionParameterEditor(Composite parent, Class<?> binding, Validator validator) {
		super(binding);
		this.validator = validator;

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).create());

		text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(GridDataFactory.fillDefaults().indent(7, 0).grab(true, false).create());
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String validationResult = valueChanged(text.getText());
				// show or hide decoration
				if (validationResult != null) {
					decoration.setDescriptionText(validationResult);
					decoration.show();
				}
				else
					decoration.hide();
			}
		});

		// create decoration
		decoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP, composite);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR);
		decoration.setImage(fieldDecoration.getImage());
		decoration.hide();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		this.text.setText(text == null ? "" : text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.editors.AbstractBindingValidatingEditor#additionalValidate(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	protected String additionalValidate(String stringValue, Object objectValue) {
		if (validator != null)
			return validator.validate(stringValue);
		else
			return null;
	}
}
