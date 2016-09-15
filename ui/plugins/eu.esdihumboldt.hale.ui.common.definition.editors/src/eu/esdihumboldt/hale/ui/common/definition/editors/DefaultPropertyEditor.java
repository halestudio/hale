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

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.codelist.CodeList;
import eu.esdihumboldt.hale.common.codelist.CodeList.CodeEntry;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint;
import eu.esdihumboldt.hale.ui.codelist.internal.CodeListUIPlugin;
import eu.esdihumboldt.hale.ui.codelist.selector.CodeListSelectionDialog;
import eu.esdihumboldt.hale.ui.codelist.service.CodeListService;
import eu.esdihumboldt.util.validator.Validator;

/**
 * A default attribute editor using binding, enumeration and validation
 * constraints.
 * 
 * @author Kai Schwierczek
 */
public class DefaultPropertyEditor extends AbstractBindingValidatingEditor<Object> {

	// XXX generic version instead?

	private final PropertyDefinition property;
	private final EntityDefinition entity;

	private final Collection<String> enumerationValues;
	private ArrayList<Object> values = null;
	private final boolean otherValuesAllowed;
	private final Validator validator;

	private Composite composite;
	private ComboViewer viewer;
	private ControlDecoration decoration;

	private Class<?> binding;
	private final ConversionService cs = HalePlatform.getService(ConversionService.class);

	private CodeList codeList;
	private final String codeListNamespace;
	private final String codeListName;

	/**
	 * Creates an attribute editor for the given type.
	 * 
	 * @param parent the parent composite
	 * @param property the property
	 * @param entity the property entity definition representing the property,
	 *            may be <code>null</code> if unknown or unavailable, needed for
	 *            code list assignment support
	 */
	public DefaultPropertyEditor(Composite parent, PropertyDefinition property,
			EntityDefinition entity) {
		super(property.getPropertyType().getConstraint(Binding.class).getBinding());
		this.property = property;
		this.entity = entity;
		TypeDefinition type = property.getPropertyType();
		binding = type.getConstraint(Binding.class).getBinding();
		validator = type.getConstraint(ValidationConstraint.class).getValidator();
		Enumeration<?> enumeration = type.getConstraint(Enumeration.class);
		otherValuesAllowed = enumeration.isAllowOthers();

		String codeListNamespace = null;
		try {
			codeListNamespace = property.getParentType().getName().getNamespaceURI();
		} catch (Exception e) {
			// ignore any case where parent type may be null
		}
		this.codeListNamespace = codeListNamespace;
		String propertyName = property.getName().getLocalPart();
		codeListName = Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1)
				+ "Value"; //$NON-NLS-1$

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
						// value is either not convertable to string or the
						// string value
						// is not convertable to the target binding.
					}
				}
		}
		else
			enumerationValues = null;

		composite = new Composite(parent, SWT.NONE);
		int numColumns = (entity == null) ? (1) : (2);
		composite.setLayout(
				GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(numColumns).create());

		viewer = new ComboViewer(composite,
				(otherValuesAllowed ? SWT.NONE : SWT.READ_ONLY) | SWT.BORDER);
		viewer.getControl().setLayoutData(
				GridDataFactory.fillDefaults().indent(7, 0).grab(true, false).create());
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof CodeEntry) {
					CodeEntry e = (CodeEntry) element;
					if (e.getName().equals(e.getIdentifier()))
						return e.getName();
					else
						return e.getName() + " (" + e.getIdentifier() + ")";
				}
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
						viewer.getCombo()
								.setToolTipText(entry.getName() + ":\n\n" + entry.getDescription()); //$NON-NLS-1$
						return;
					}
				}

				viewer.getCombo().setToolTipText(null);
			}
		});
		viewer.setInput(values);
		if (otherValuesAllowed)
			viewer.getCombo().setText("");

		// create decoration
		decoration = new ControlDecoration(viewer.getControl(), SWT.LEFT | SWT.TOP, composite);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		decoration.setImage(fieldDecoration.getImage());
		decoration.hide();

		viewer.getCombo().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String newValue = viewer.getCombo().getText();
				if (viewer.getSelection() != null && !viewer.getSelection().isEmpty()
						&& viewer.getSelection() instanceof IStructuredSelection) {
					Object selection = ((IStructuredSelection) viewer.getSelection())
							.getFirstElement();
					if (selection instanceof CodeEntry)
						newValue = ((CodeEntry) selection).getIdentifier();
				}

				String validationResult = valueChanged(newValue);
				// show or hide decoration
				if (validationResult != null) {
					decoration.setDescriptionText(validationResult);
					decoration.show();
				}
				else
					decoration.hide();
			}
		});

		// set initial selection (triggers modify event -> gets validated
		if (values != null && values.size() > 0)
			viewer.setSelection(new StructuredSelection(values.iterator().next()));

		// add code list selection button
		final Image assignImage = CodeListUIPlugin.getImageDescriptor("icons/assign_codelist.gif") //$NON-NLS-1$
				.createImage();

		if (entity != null) {
			Button assign = new Button(composite, SWT.PUSH);
			assign.setImage(assignImage);
			assign.setToolTipText("Assign a code list"); //$NON-NLS-1$
			assign.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					final Display display = Display.getCurrent();
					CodeListSelectionDialog dialog = new CodeListSelectionDialog(
							display.getActiveShell(), codeList,
							MessageFormat.format("Please select a code list to assign to {0}",
									DefaultPropertyEditor.this.property.getDisplayName()));
					if (dialog.open() == CodeListSelectionDialog.OK) {
						CodeList newCodeList = dialog.getCodeList();
						CodeListService codeListService = PlatformUI.getWorkbench()
								.getService(CodeListService.class);

						codeListService.assignEntityCodeList(DefaultPropertyEditor.this.entity,
								newCodeList);

						updateCodeList();
					}
				}
			});
		}

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
			Label inputInfo = new Label(composite, SWT.WRAP);
			inputInfo.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
					.hint(400, SWT.DEFAULT).create());
			inputInfo.setText(infoText.toString());
		}
	}

	private void updateCodeList() {
		values = new ArrayList<Object>();
		if (enumerationValues != null)
			values.addAll(enumerationValues);

		CodeListService clService = PlatformUI.getWorkbench().getService(CodeListService.class);
		if (entity != null) {
			codeList = clService.findCodeListByEntity(entity);
		}
		if (codeList == null)
			codeList = clService.findCodeListByIdentifier(codeListNamespace, codeListName);
		if (codeList != null) {
			// XXX check values against validator and binding?
			// codeList values are only added if no enumeration is present or
			// other values are explicitly allowed by the enumerations
			if (values.isEmpty() || otherValuesAllowed)
				values.addAll(codeList.getEntries());
		}
		values.trimToSize();

		// save combo text to restore after setting the new input
		String oldValue = viewer.getCombo().getText();
		viewer.setInput(values);
		viewer.getCombo().setText(oldValue);
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
		if (values == null || values.isEmpty())
			viewer.getCombo().setText(text);
		else if (enumerationValues != null) {
			// enumeration -> ignore value if it isn't valid
			if (otherValuesAllowed || enumerationValues.contains(text))
				viewer.getCombo().setText(text);
		}
		else {
			// a code list is assigned -> try to find matching entry
			CodeEntry entry = codeList.getEntryByIdentifier(text);
			if (entry != null)
				viewer.setSelection(new StructuredSelection(entry));
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.definition.editors.AbstractBindingValidatingEditor#additionalValidate(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	protected String additionalValidate(String stringValue, Object objectValue) {
		return validator.validate(objectValue);
	}
}
