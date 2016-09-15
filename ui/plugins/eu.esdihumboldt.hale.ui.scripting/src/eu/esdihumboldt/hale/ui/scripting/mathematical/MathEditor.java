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

package eu.esdihumboldt.hale.ui.scripting.mathematical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.common.align.model.condition.impl.BindingCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.PropertyValueImpl;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.scripting.Script;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.editors.AbstractAttributeEditor;
import eu.esdihumboldt.hale.ui.transformation.TransformationVariableReplacer;

/**
 * Editor for math scripts.
 * 
 * @author Kai Schwierczek
 */
public class MathEditor extends AbstractAttributeEditor<String> {

	private final Composite composite;
	private Text textField;
	private TableViewer varTable;
	private Collection<PropertyEntityDefinition> variables = Collections.emptySet();
	private boolean valid;
	private final Script script;
	private String currentValue = "";
	private final ControlDecoration decorator;

	/**
	 * Default constructor.
	 * 
	 * @param parent the parent composite
	 * @param script the script object
	 */
	public MathEditor(Composite parent, Script script) {
		this.script = script;

		composite = new Composite(parent, SWT.NONE);
		// left margin 5 for control decoration
		composite.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).create());

		// input field
		textField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textField.setLayoutData(
				GridDataFactory.fillDefaults().grab(true, false).indent(7, 0).create());

		// control decoration
		decorator = new ControlDecoration(textField, SWT.LEFT | SWT.TOP, composite);
		// set initial status
		decorator.hide();
		// set image
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		decorator.setImage(fieldDecoration.getImage());

		textField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// update value
				String newValue = textField.getText();
				fireValueChanged(VALUE, currentValue, newValue);
				currentValue = newValue;

				validate();
			}
		});

		// variables
		Label label = new Label(composite, SWT.NONE);
		label.setText("Available variables (double click to insert)");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		// variables table
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);
		varTable = new TableViewer(tableComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		varTable.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				PropertyEntityDefinition def = (PropertyEntityDefinition) element;
				Class<?> binding = def.getDefinition().getPropertyType()
						.getConstraint(Binding.class).getBinding();

				return BindingCondition.isCompatibleClass(Double.class, true, binding, true);
			}
		});
		TableViewerColumn column = new TableViewerColumn(varTable, SWT.NONE);
		columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(1, false));
		varTable.setContentProvider(ArrayContentProvider.getInstance());
		varTable.setLabelProvider(new DefinitionLabelProvider(null, true, true) {

			/**
			 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return MathEditor.this.script.getVariableName((PropertyEntityDefinition) element);
			}
		});
		varTable.getTable().addMouseListener(new MouseAdapter() {

			/**
			 * @see MouseAdapter#mouseDoubleClick(MouseEvent)
			 */
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = varTable.getTable().getSelectionIndex();
				if (index >= 0) {
					String var = varTable.getTable().getItem(index).getText();
					textField.insert(var);
					textField.setFocus();
				}
			}
		});

		validate();
	}

	/**
	 * Validates the current input against the currently available variables.
	 */
	private void validate() {
		String scriptStr = currentValue;
		String result = null;

		// replace variables
		try {
			scriptStr = new TransformationVariableReplacer().replaceVariables(scriptStr);
		} catch (Exception e) {
			result = e.getLocalizedMessage();
		}

		if (result == null) {
			result = MathEditor.this.script.validate(scriptStr, createPropertyValues(),
					HaleUI.getServiceProvider());
		}

		boolean oldValid = valid;
		valid = result == null;
		if (result == null)
			decorator.hide();
		else {
			decorator.setDescriptionText(result);
			decorator.show();
		}
		if (valid != oldValid) {
			fireStateChanged(IS_VALID, oldValid, valid);
		}
	}

	/**
	 * Returns an {@link Iterable} for the current variables for use with the
	 * {@link Script}.
	 * 
	 * @return an {@link Iterable} for the current variables for use with the
	 *         {@link Script}
	 */
	protected Iterable<PropertyValue> createPropertyValues() {
		Collection<PropertyValue> result = new ArrayList<PropertyValue>(variables.size());
		// using double results in no /0 exceptions because of stuff like
		// 1/(a-b)
		Double one = Double.valueOf(1);
		for (PropertyEntityDefinition property : variables)
			result.add(new PropertyValueImpl(one, property));
		return result;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return composite;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String value) {
		textField.setText(value);
		currentValue = value;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValue()
	 */
	@Override
	public String getValue() {
		return currentValue;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) {
		setValue(text);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getAsText()
	 */
	@Override
	public String getAsText() {
		return getValue();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#isValid()
	 */
	@Override
	public boolean isValid() {
		return valid;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.editors.AbstractAttributeEditor#setVariables(java.util.Collection)
	 */
	@Override
	public void setVariables(Collection<PropertyEntityDefinition> properties) {
		variables = properties;
		varTable.setInput(variables);
		validate();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.common.AttributeEditor#getValueType()
	 */
	@Override
	public String getValueType() {
		return script.getId();
	}
}
