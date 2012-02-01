/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.common.definition.internal.editors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint;
import eu.esdihumboldt.hale.ui.common.definition.AbstractAttributeEditor;
import eu.esdihumboldt.util.validator.Validator;

/**
 * A default attribute editor using binding, enumeration and validation
 * constraints.
 * 
 * @author Kai Schwierczek
 */
public class DefaultAttributeEditor extends AbstractAttributeEditor<Object> {
	// XXX generic version instead?

	private final Class<?> binding;
	private final Collection<String> values;
	private final boolean otherValuesAllowed;
	private final Validator validator;
	private final ConversionService cs = OsgiUtils.getService(ConversionService.class);

	private Composite composite;
	private ComboViewer viewer;
	private ControlDecoration decoration;
	private String stringValue;
	private Object objectValue;
	private String validationResult;

	/**
	 * Creates an attribute editor for the given type.
	 * 
	 * @param parent the parent composite
	 * @param type the type
	 */
	public DefaultAttributeEditor(Composite parent, TypeDefinition type) {
		binding = type.getConstraint(Binding.class).getBinding();
		validator = type.getConstraint(ValidationConstraint.class).getValidator();
		Enumeration<?> enumeration = type.getConstraint(Enumeration.class);
		otherValuesAllowed = enumeration.isAllowOthers();
		if (enumeration.getValues() != null) {
			values = new ArrayList<String>(enumeration.getValues().size());
			// check values against validator and binding
			for (Object o : enumeration.getValues())
				if (validator.validate(o) == null) {
					try {
						String stringValue = cs.convert(o, String.class);
						cs.convert(stringValue, binding);
						values.add(stringValue);
					} catch (ConversionException ce) {
						// value is either not convertable to string or the string value
						// is not convertable to the target binding.
					}
				}
		} else
			values = null;

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().create());

		// info on what inputs are valid
		Label inputInfo = new Label(composite, SWT.NONE);
		StringBuilder infoText = new StringBuilder();
		// every string is convertable to string -> leave that out
		if (!binding.equals(String.class))
			infoText.append("Input must be convertable to ").append(binding).append('.');
		// every input is valid -> leave that out
		if (!validator.isAlwaysTrue()) {
			if (infoText.length() > 0)
				infoText.append('\n');
			infoText.append(validator.getDescription());
		}
		inputInfo.setText(infoText.toString());

		viewer = new ComboViewer(composite, (otherValuesAllowed ? SWT.NONE : SWT.READ_ONLY) | SWT.BORDER);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).grab(true, false).create());
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(values);

		// create decoration
		decoration = new ControlDecoration(viewer.getControl(), SWT.LEFT | SWT.TOP, composite);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR);
		decoration.setImage(fieldDecoration.getImage());
		decoration.hide();

		viewer.getCombo().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String oldValue = stringValue;
				String newValue = viewer.getCombo().getText();
				valueChanged(oldValue, newValue);
			}
		});

		// set initial selection (triggers modify event -> gets validated
		if (values != null && values.size() > 0)
			viewer.setSelection(new StructuredSelection(values.iterator().next()));
	}

	/**
	 * Updates the local value, valid status and fires necessary events.
	 * 
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	private void valueChanged(String oldValue, String newValue) {
		// get old valid status
		boolean wasValid = isValid();
		// set new value
		stringValue = newValue;
		// validate it
		validate();
		// check whether valid status changed
		boolean validChanged = false;
		if (wasValid != isValid())
			validChanged = true;
		// fire events
		fireValueChanged(VALUE, oldValue, newValue);
		if (validChanged)
			fireStateChanged(IS_VALID, wasValid, !wasValid);
	}

	/**
	 * Validates the current string value and sets validationResult.<br>
	 * Also sets object result if possible and updates the ControlDecoration.
	 */
	private void validate() {
		validationResult = null;

		// check binding first
		try {
			// for example boolean converter returns null for empty string...
			objectValue = cs.convert(stringValue, binding);
			if (objectValue == null)
				validationResult = stringValue + " cannot be converted to " + binding.getSimpleName();
		} catch (ConversionException ce) {
			objectValue = null;
			validationResult = stringValue + " cannot be converted to " + binding.getSimpleName();
		}

		// validators
		if (validationResult == null)
			validationResult = validator.validate(objectValue);

		// show or hide decoration
		if (validationResult != null) {
			decoration.setDescriptionText(validationResult);
			decoration.show();
		} else
			decoration.hide();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		setAsText(cs.convert(value, String.class));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditor#getValue()
	 * 
	 * @throws IllegalStateException if the current input is not valid
	 */
	@Override
	public Object getValue() {
		if (isValid())
			return objectValue;
		else
			throw new IllegalStateException();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		// Simply set as string IF other values are allowed. Check against enumeration otherwise.
		if (otherValuesAllowed || values.contains(text))
			viewer.getCombo().setText(text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditor#getAsText()
	 * 
	 * @throws IllegalStateException if the current input is not valid
	 */
	@Override
	public String getAsText() {
		if (isValid()) {
			// return converted value, as that SHOULD be XML conform
			// in contrast to input value where the converter maybe allows more.
			return cs.convert(objectValue, String.class);
		} else
			throw new IllegalStateException();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return validationResult == null;
	}
}
