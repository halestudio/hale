/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.functions.core.userdefined;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.utils.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleComposedCellWizardPage;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.CoreFunctionWizardsPlugin;

/**
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class UserDefinedFunctionWizardPage extends
		AbstractSingleComposedCellWizardPage {
	
	/**
	 * The key represents the name of the template, the value list represents 
	 * the name of each of the possible parameters.
	 */
	private final Map<String, List<String>> udfTemplates = new TreeMap<String, List<String>>();
	
	private ComboViewer templates;
	private Text udfName;
	private Table table;
	
	private Button saveButton;
	private Button removeButton;
	
	private TableEditor nameEditor;
	
	private final Image addImage = 
		CoreFunctionWizardsPlugin.getImageDescriptor("icons/add.gif").createImage();
	private final Image removeImage = 
		CoreFunctionWizardsPlugin.getImageDescriptor("icons/remove.gif").createImage();
	private final Image saveImage = 
		CoreFunctionWizardsPlugin.getImageDescriptor("icons/save.gif").createImage();

	private final String udfSeparator = "<udfFunction>";
	private final String nameParameterSeparator = "<nameParameter>";
	private final String parameterSeparator = "<parameter>";

	/**
	 * Creates the page with a defined pageName.
	 * @param pageName the name for the page to use.
	 */
	public UserDefinedFunctionWizardPage(String pageName) {
		super(pageName);
		super.setTitle(pageName);
		super.setDescription("Configure your User Defined Function");
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// load already defined UDF templates
		IPreferenceStore preferences = CoreFunctionWizardsPlugin.plugin.getPreferenceStore();
		String storedTemplates = preferences.getString("eu.esdihumboldt.udfPreferences");
		if (storedTemplates != null && !storedTemplates.equals("")) {
			String[] separatedTemplates = storedTemplates.split(udfSeparator);
			for (String tpl : separatedTemplates) {
				// separate name from parameters
				String[] nameParameters = tpl.split(nameParameterSeparator);
				if (nameParameters.length == 2) {
					this.udfTemplates.put(nameParameters[0], 
							Arrays.asList(nameParameters[1].split(parameterSeparator)));
				}
			}
		}
		
		// Basic layout elements
		super.initializeDialogUnits(parent);
		setControl(parent);
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(1, false));
		
		DefinitionLabelFactory dlf = (DefinitionLabelFactory) PlatformUI.getWorkbench().getService(DefinitionLabelFactory.class);
		
		// Group 0: Information on the selected Entities
		Group entityInfoGroup = new Group(page, SWT.NONE);
		entityInfoGroup.setText("Selected entities:");
		entityInfoGroup.setLayout(new GridLayout(2, false));
		GridData selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		entityInfoGroup.setLayoutData(selectionAreaGD);
		entityInfoGroup.setSize(entityInfoGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		entityInfoGroup.setFont(page.getFont());
		
		final Label sourceEntitiesLabel = new Label(entityInfoGroup, SWT.NONE);
		sourceEntitiesLabel.setText("Source:");
		
		Control sourceLabel = dlf.createLabel(entityInfoGroup, 
				getParent().getFirstSourceItem().getDefinition(), true);
		sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		final Label targetEntitiesLabel = new Label(entityInfoGroup, SWT.NONE);
		targetEntitiesLabel.setText("Target:");
		
		Control targetLabel = dlf.createLabel(entityInfoGroup, 
				getParent().getFirstTargetItem().getDefinition(), true);
		targetLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.LEFT, false, false));
		
		// Group 1: Selection of a UDF template (are stored using eclipse preferences store)
		Group selectUdfTemplateGroup = new Group(page, SWT.NONE);
		selectUdfTemplateGroup.setText("Select or create a template for your function:");
		selectUdfTemplateGroup.setLayout(new GridLayout(4, false));
		selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		selectUdfTemplateGroup.setLayoutData(selectionAreaGD);
		selectUdfTemplateGroup.setSize(selectUdfTemplateGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		selectUdfTemplateGroup.setFont(page.getFont());
		
		Combo combo = new Combo(selectUdfTemplateGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		templates = new ComboViewer(combo);
		templates.setContentProvider(new ArrayContentProvider());
		templates.setInput(udfTemplates.keySet());
		templates.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() != null) {
					String key = ((StructuredSelection)event.getSelection()).getFirstElement().toString();
					udfName.setText(key);
					table.removeAll();
					for (String paramName : udfTemplates.get(key)) {
						TableItem item = new TableItem (table, SWT.NONE);
						item.setText (0, paramName);
					}
					table.setEnabled(true);
					Control oldEditor = nameEditor.getEditor();
					if (oldEditor != null) oldEditor.dispose();
					removeButton.setEnabled(true);
				}
			}
			
		});
		
		// add UDF template
		Button addButton = new Button(selectUdfTemplateGroup, SWT.PUSH);
		addButton.setImage(addImage);
		addButton.setToolTipText("Add a new UDF template");
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				table.clearAll();
				udfName.setText("");
				table.setEnabled(true);
				table.removeAll();
				for (int i = 0; i < 20; i++) {
					TableItem item = new TableItem (table, SWT.NONE);
					item.setText (0, "");
					item.setText (1, "");
				}
				Control oldEditor = nameEditor.getEditor();
				if (oldEditor != null) oldEditor.dispose();
				udfName.setEnabled(true);
				saveButton.setEnabled(true);
			}
			
		});
		
		// save current UDF template
		this.saveButton = new Button(selectUdfTemplateGroup, SWT.PUSH);
		this.saveButton.setImage(saveImage);
		this.saveButton.setEnabled(false);
		this.saveButton.setToolTipText("Save the current UDF as a template");
		this.saveButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (udfName != null && udfName.getText() != null 
						&& !udfName.getText().equals("")) {
					List<String> paramNames = new ArrayList<String>();
					for (int i = 0; i < table.getItemCount(); i++) {
						String result = table.getItem(i).getText(0);
						if (result != null && !result.equals("")) {
							paramNames.add(result);
						}
					}
					udfTemplates.put(udfName.getText(), paramNames);
					templates.setInput(udfTemplates.keySet());
				}
			}
			
		});
		
		// remove UDF template
		this.removeButton = new Button(selectUdfTemplateGroup, SWT.PUSH);
		this.removeButton.setImage(removeImage);
		this.removeButton.setEnabled(false);
		this.removeButton.setToolTipText("Remove currently selected UDF template");
		this.removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				udfName.setEnabled(false);
				udfName.setText("");
				udfTemplates.remove(((StructuredSelection)templates.getSelection()).getFirstElement().toString());
				templates.setInput(udfTemplates.keySet());
				table.removeAll();
				Control oldEditor = nameEditor.getEditor();
				if (oldEditor != null) oldEditor.dispose();
			}
			
		});
		
		// Group 2: Parameters of the UDF that has been selected
		Group udfParametersGroup = new Group(page, SWT.NONE);
		udfParametersGroup.setText("Configure your function:");
		udfParametersGroup.setLayout(new GridLayout());
		selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		udfParametersGroup.setLayoutData(selectionAreaGD);
		udfParametersGroup.setSize(selectUdfTemplateGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		udfParametersGroup.setFont(page.getFont());
		
		final Label udfNameLabel = new Label(udfParametersGroup, SWT.NONE);
		udfNameLabel.setText("UDF template name:");
		udfNameLabel.setToolTipText("Enter the (qualified) name for your User Defined Function");
		
		this.udfName = new Text (udfParametersGroup, SWT.BORDER | SWT.SINGLE);
		this.udfName.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		this.udfName.setEnabled(false);
		
		final Label udfParametersLabel = new Label(udfParametersGroup, SWT.NONE);
		udfParametersLabel.setText("UDF parameters:");
		udfParametersLabel.setToolTipText("Enter the names and values of your User Defined Function's parameters");
		
		this.table = new Table (udfParametersGroup, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		this.table.setLinesVisible (true);
		this.table.setHeaderVisible (true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);
		
		TableColumn columnName = new TableColumn(table, SWT.NONE);
		columnName.setText("Parameter name");
		columnName.setWidth(125);
		TableColumn columnValue = new TableColumn(table, SWT.NONE);
		columnValue.setText("Parameter value");
		columnValue.setWidth(340);
		
		this.nameEditor = new TableEditor(table);
		this.nameEditor.horizontalAlignment = SWT.LEFT;
		this.nameEditor.grabHorizontal = true;
		this.nameEditor.minimumWidth = 50;
		
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// determine column to edit
				final int column = e.x;
				
				// Clean up any previous editor control
				Control oldEditor = nameEditor.getEditor();
				if (oldEditor != null) oldEditor.dispose();
		
				// Identify the selected row
				TableItem item = (TableItem)e.item;
				if (item == null) return;
		
				// The control that will be the editor must be a child of the Table
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(column));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text)nameEditor.getEditor();
						nameEditor.getItem().setText(column, text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				nameEditor.setEditor(newEditor, item, column);
			}
		});
		
		
		// create some default rows.
		for (int i = 0; i < 20; i++) {
			TableItem item = new TableItem (table, SWT.NONE);
			item.setText (0, "");
			item.setText (1, "");
		}
		
		this.table.setEnabled(false);
	}

	/**
	 * @return the Map with the currently defined Udf templates.
	 */
	public String getEncodedUdfTemplates() {
		StringBuffer result = new StringBuffer();
		boolean firstUdf = true;
		for (String key : this.udfTemplates.keySet()) {
			if (!firstUdf) {
				result.append(this.udfSeparator);
			}
			else {
				firstUdf = false;
			}
			result.append(key);
			result.append(this.nameParameterSeparator);
			boolean first = true;
			for (String paramName : this.udfTemplates.get(key)) {
				if (!first) {
					result.append(this.parameterSeparator);
				}
				else {
					first = false;
				}
				result.append(paramName);
			}
		}
		return result.toString();
	}
	
	

}
