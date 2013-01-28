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

package eu.esdihumboldt.hale.ui.functions.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Base parameter page for parameter pages that contain a listing of source
 * types which can be put together to a target value.
 * 
 * @param <T> the type of the text field/editor
 * 
 * @author Kai Schwierczek
 */
public abstract class SourceListParameterPage<T> extends
		HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements ParameterPage {

	private String initialValue = "";
	private T textField;
	private TableViewer varTable;
	private EntityDefinition[] variables = new EntityDefinition[0];

	/**
	 * @see HaleWizardPage#HaleWizardPage(String, String, ImageDescriptor)
	 */
	protected SourceListParameterPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	/**
	 * @see HaleWizardPage#HaleWizardPage(String)
	 */
	protected SourceListParameterPage(String pageName) {
		super(pageName);
	}

	/**
	 * Should return the parameter which should be configured using all source
	 * properties.
	 * 
	 * @return the parameter name
	 */
	protected abstract String getParameterName();

	/**
	 * Should return the name of the source property which should be used.
	 * 
	 * @return the source property name
	 */
	protected abstract String getSourcePropertyName();

	/**
	 * Subclasses can configure the text field to for example add some
	 * validation mechanism.
	 * 
	 * @param textField the text field to configure
	 */
	protected void configure(T textField) {
		// default: do nothing
	}

	/**
	 * This gets called for all variables.<br>
	 * Subclasses can change how they are displayed here.<br>
	 * The default format is like "part1.part2.name".
	 * 
	 * @param variable the variable
	 * @return the modified name
	 */
	protected String getVariableName(EntityDefinition variable) {
		if (variable.getPropertyPath() != null && !variable.getPropertyPath().isEmpty()) {
			List<String> names = new ArrayList<String>();
			for (ChildContext context : variable.getPropertyPath()) {
				names.add(context.getChild().getName().getLocalPart());
			}
			String longName = Joiner.on('.').join(names);
			return longName;
		}
		else
			return variable.getDefinition().getDisplayName();
	}

	/**
	 * This gets called, when the user chose other source properties.
	 * 
	 * @param variables the new source properties
	 */
	protected void sourcePropertiesChanged(EntityDefinition[] variables) {
		// do nothing by default
	}

	/**
	 * @see ParameterPage#setParameter(Set, ListMultimap)
	 */
	@Override
	public void setParameter(Set<FunctionParameter> params,
			ListMultimap<String, ParameterValue> initialValues) {
		for (FunctionParameter param : params) {
			if (param.getName().equals(getParameterName())) {
				String description = param.getDescription();
				if (description != null) {
					setMessage(description);
				}
				String displayName = param.getDisplayName();
				if (displayName != null) {
					setTitle(displayName);
				}
				break;
			}
		}

		if (initialValues != null) {
			List<ParameterValue> initialData = initialValues.get(getParameterName());
			if (initialData.size() > 0)
				initialValue = initialData.get(0).getStringValue();
		}
	}

	/**
	 * @see ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, ParameterValue> getConfiguration() {
		ListMultimap<String, ParameterValue> params = ArrayListMultimap.create();
		params.put(getParameterName(), new ParameterValue(getText(textField)));
		return params;
	}

	/**
	 * @see HaleWizardPage#onShowPage(boolean)
	 */
	@Override
	protected void onShowPage(boolean firstShow) {
		Cell cell = getWizard().getUnfinishedCell();

		// update variables as they could have changed
		List<? extends Entity> sourceEntities = cell.getSource().get(getSourcePropertyName());
		variables = new EntityDefinition[sourceEntities.size()];
		Iterator<? extends Entity> iter = sourceEntities.iterator();
		for (int i = 0; i < variables.length; i++)
			variables[i] = iter.next().getDefinition();

		varTable.setInput(variables);

		// inform subclasses
		sourcePropertiesChanged(variables);

		((Composite) getControl()).layout();
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().create());

		// input field
		textField = createAndLayoutTextField(page);

		// let subclasses for example add validation
		configure(textField);

		setText(textField, initialValue);

		// variables
		Label label = new Label(page, SWT.NONE);
		label.setText("Available variables (double click to insert)");
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		// variables table
		Composite tableComposite = new Composite(page, SWT.NONE);
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);
		varTable = new TableViewer(tableComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		TableViewerColumn column = new TableViewerColumn(varTable, SWT.NONE);
		columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(1, false));
		varTable.setContentProvider(ArrayContentProvider.getInstance());
		varTable.setLabelProvider(new DefinitionLabelProvider(true, true) {

			/**
			 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				return getVariableName((EntityDefinition) element);
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
					// let subclass modify variable
					insertTextAtCurrentPos(textField, var);
//					textField.insert(var);
//					textField.setFocus();
				}
			}
		});
	}

	/**
	 * Set the text of the text field to the given value.
	 * 
	 * @param textField the text field
	 * @param value the value to set as text
	 */
	protected abstract void setText(T textField, String value);

	/**
	 * Get the current text of the text field.
	 * 
	 * @param textField the text field
	 * @return the current text of the text field
	 */
	protected abstract String getText(T textField);

	/**
	 * Insert a given text at the current position of the given text field.
	 * 
	 * @param textField the text field
	 * @param insert the text to insert
	 */
	protected abstract void insertTextAtCurrentPos(T textField, String insert);

	/**
	 * Create and text field and layout it.
	 * 
	 * @param parent the parent composite, it has a one-column grid layout
	 * 
	 * @return the created text field
	 */
	protected abstract T createAndLayoutTextField(Composite parent);

	/**
	 * Get the text editor/field.
	 * 
	 * @return the text field
	 */
	protected T getTextField() {
		return textField;
	}

}
