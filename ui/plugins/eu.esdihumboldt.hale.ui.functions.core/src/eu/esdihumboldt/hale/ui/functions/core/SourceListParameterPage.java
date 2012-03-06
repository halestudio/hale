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

package eu.esdihumboldt.hale.ui.functions.core;

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
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.HaleWizardPage;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.function.generic.AbstractGenericFunctionWizard;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Base parameter page for parameter pages that contain a listing of source
 * types which can be put together to a target value.
 * 
 * @author Kai Schwierczek
 */
public abstract class SourceListParameterPage extends HaleWizardPage<AbstractGenericFunctionWizard<?, ?>> implements
		ParameterPage {
	
	private String initialValue = "";
	private Text textField;
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
	 * Should return the parameter which should be configured using all source properties.
	 * 
	 * @return the parameter name
	 */
	protected abstract String getParameterName() ;

	/**
	 * Should return the name of the source property which should be used.
	 * 
	 * @return the source property name
	 */
	protected abstract String getSourcePropertyName() ;

	/**
	 * Subclasses can override this method to specify, that the text field should have 
	 * multiple lines. By default it is not.
	 * 
	 * @return true if the text field should have multiple lines.
	 */
	protected boolean useMultilineInput() {
		return false;
	}

	/**
	 * Subclasses can configure the text field to for example add some validation mechanism.
	 * 
	 * @param textField the text field to configure
	 */
	protected void configure(Text textField) {
		// default: do nothing
	}

	/**
	 * This gets called when the user chose a source property to insert.<br>
	 * Subclasses can change the inserted string here.
	 * 
	 * @param value the value that should get inserted
	 * @return the modified value which gets inserted
	 */
	protected String insert(String value) {
		// give EntityDefinition instead? More freedom for subclasses...
		return value;
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
	public void setParameter(Set<FunctionParameter> params, ListMultimap<String, String> initialValues) {
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
			List<String> initialData = initialValues.get(getParameterName());
			if (initialData.size() > 0)
				initialValue = initialData.get(0);
		}
	}

	/**
	 * @see ParameterPage#getConfiguration()
	 */
	@Override
	public ListMultimap<String, String> getConfiguration() {
		ListMultimap<String, String> params = ArrayListMultimap.create();
		params.put(getParameterName(), textField.getText());
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

		// inform subclasses
		sourcePropertiesChanged(variables);

		varTable.setInput(variables);

		((Composite) getControl()).layout();
	}

	/**
	 * @see HaleWizardPage#createContent(Composite)
	 */
	@Override
	protected void createContent(Composite page) {
		page.setLayout(GridLayoutFactory.swtDefaults().create());
		
		// input field
		int lineStyle = useMultilineInput() ? SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE;
		textField = new Text(page, lineStyle | SWT.BORDER);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, useMultilineInput()));

		// let subclasses for example add validation
		configure(textField);

		textField.setText(initialValue);

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
		varTable.setLabelProvider(new DefinitionLabelProvider(true, true));
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
					textField.insert(insert(var));
					textField.setFocus();
				}
			}
		});
	}
}
