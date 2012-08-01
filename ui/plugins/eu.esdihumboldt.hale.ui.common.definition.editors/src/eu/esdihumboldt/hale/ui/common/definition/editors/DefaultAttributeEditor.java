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

package eu.esdihumboldt.hale.ui.common.definition.editors;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint;
import eu.esdihumboldt.hale.ui.codelist.internal.CodeListUIPlugin;
import eu.esdihumboldt.hale.ui.codelist.selector.CodeListSelectionDialog;
import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;
import eu.esdihumboldt.hale.ui.common.editors.AbstractEditor;
import eu.esdihumboldt.util.validator.Validator;

/**
 * A default attribute editor using binding, enumeration and validation
 * constraints.
 * 
 * @author Kai Schwierczek
 */
public class DefaultAttributeEditor extends AbstractEditor<Object> {
	// XXX generic version instead?

	private final PropertyDefinition property;
	private final Class<?> binding;
	private final Collection<String> enumerationValues;
	private ArrayList<Object> values;
	private final boolean otherValuesAllowed;
	private final Validator validator;
	private final ConversionService cs = OsgiUtils.getService(ConversionService.class);

	private Composite composite;
	private ComboViewer viewer;
	private ControlDecoration decoration;
	private String stringValue;
	private Object objectValue;
	private boolean validated = false;
	private String validationResult;

	private CodeList codeList;
	private final String codeListNamespace;
	private final String codeListName;

	/**
	 * Creates an attribute editor for the given type.
	 * 
	 * @param parent the parent composite
	 * @param property the property
	 */
	public DefaultAttributeEditor(Composite parent, PropertyDefinition property) {
		this.property = property;
		TypeDefinition type = property.getPropertyType();
		binding = type.getConstraint(Binding.class).getBinding();
		validator = type.getConstraint(ValidationConstraint.class).getValidator();
		Enumeration<?> enumeration = type.getConstraint(Enumeration.class);
		otherValuesAllowed = enumeration.isAllowOthers();

		codeListNamespace = property.getParentType().getName().getNamespaceURI();
		String propertyName = property.getName().getLocalPart();
		codeListName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1) + "Value"; //$NON-NLS-1$	

		// add enumeration info
		if (enumeration.getValues() != null) {
			enumerationValues = new ArrayList<String>(enumeration.getValues().size());
			// check values against validator and binding
			for (Object o : enumeration.getValues())
				if (validator.validate(o) == null) {
					try {
						String stringValue = cs.convert(o, String.class);
						cs.convert(stringValue, binding);
						enumerationValues.add(stringValue);
					} catch (ConversionException ce) {
						// value is either not convertable to string or the string value
						// is not convertable to the target binding.
					}
				}
		} else
			enumerationValues = null;

		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(2).create());

		viewer = new ComboViewer(composite, (otherValuesAllowed ? SWT.NONE : SWT.READ_ONLY) | SWT.BORDER);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().indent(5, 0).grab(true, false).create());
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				// XXX show more information out of the CodeEntry?
				if (element instanceof CodeEntry)
					return ((CodeEntry) element).getName();
				else
					return super.getText(element);
			}
		});
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
					Object selected = ((IStructuredSelection) selection).getFirstElement();
					if (selected instanceof CodeEntry) {
						CodeEntry entry = (CodeEntry) selected;
						viewer.getCombo().setToolTipText(entry.getName() + ":\n\n" + entry.getDescription()); //$NON-NLS-1$
						return;
					}
				}

				viewer.getCombo().setToolTipText(null);
			}
		});
		viewer.setInput(values);
		if (otherValuesAllowed) {
			viewer.getCombo().setText("");
			stringValue = "";
		}

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
				if (viewer.getSelection() != null && !viewer.getSelection().isEmpty()
						&& viewer.getSelection() instanceof IStructuredSelection) {
					Object selection = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
					if (selection instanceof CodeEntry)
						newValue = ((CodeEntry) selection).getIdentifier();
				}
				valueChanged(oldValue, newValue);
			}
		});

		// set initial selection (triggers modify event -> gets validated
		if (values != null && values.size() > 0)
			viewer.setSelection(new StructuredSelection(values.iterator().next()));

		// add code list selection button
		final Image assignImage = 
				CodeListUIPlugin.getImageDescriptor("icons/assign_codelist.gif").createImage(); //$NON-NLS-1$
		
		Button assign = new Button(composite, SWT.PUSH);
		assign.setImage(assignImage);
		assign.setToolTipText("Assign a code list"); //$NON-NLS-1$
		assign.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final Display display = Display.getCurrent();
				CodeListSelectionDialog dialog = new CodeListSelectionDialog(display.getActiveShell(), codeList,
						MessageFormat.format("Please select a code list to assign to {0}", 
								DefaultAttributeEditor.this.property.getDisplayName()));
				if (dialog.open() == CodeListSelectionDialog.OK) {
					CodeList newCodeList = dialog.getCodeList();
					CodeListService codeListService = 
							(CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);

					codeListService.assignAttributeCodeList(
							DefaultAttributeEditor.this.property.getIdentifier(), newCodeList);

					updateCodeList();
				}
			}
		});

		composite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				assignImage.dispose();
			}
		});

		// add code list info
		updateCodeList();


		// info on what inputs are valid
		StringBuilder infoText = new StringBuilder();
		// every string is convertible to string -> leave that out
		if (!binding.equals(String.class))
			infoText.append("Input must be convertable to ").append(binding).append('.');
		// every input is valid -> leave that out
		if (!validator.isAlwaysTrue()) {
			if (infoText.length() > 0)
				infoText.append('\n');
			infoText.append(validator.getDescription());
		}
		
		if (infoText.length() > 0) {
			Label inputInfo = new Label(composite, SWT.NONE);
			inputInfo.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
			inputInfo.setText(infoText.toString());
		}
	}

	private void updateCodeList() {
		// TODO how to handle enumeration + code list?
		values = new ArrayList<Object>();
		if (enumerationValues != null)
			values.addAll(enumerationValues);

		CodeListService clService = (CodeListService) PlatformUI.getWorkbench().getService(CodeListService.class);
		codeList = clService.findCodeListByAttribute(property.getIdentifier());
		if (codeList == null)
			codeList = clService.findCodeListByIdentifier(codeListNamespace, codeListName);
		if (codeList != null) {
			// XXX check values against validator and binding?
			if (values.isEmpty())
				values.addAll(codeList.getEntries());
		}
		values.trimToSize();

		viewer.setInput(values);
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
		validated = true;

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
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		setAsText(cs.convert(value, String.class));
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getValue()
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
	 * @see eu.esdihumboldt.hale.ui.common.Editor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		// Simply set as string IF other values are allowed. Check against enumeration otherwise.
		if (otherValuesAllowed || values.contains(text))
			viewer.getCombo().setText(text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.Editor#getAsText()
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
	 * @see eu.esdihumboldt.hale.ui.common.Editor#isValid()
	 */
	@Override
	public boolean isValid() {
		if (!validated)
			validate();
		return validationResult == null;
	}
}
