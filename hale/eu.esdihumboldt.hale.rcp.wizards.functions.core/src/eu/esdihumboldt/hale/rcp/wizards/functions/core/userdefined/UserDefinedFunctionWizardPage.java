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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.commons.goml.oml.ext.Parameter;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.CoreFunctionWizardsPlugin;
import eu.esdihumboldt.hale.rcp.wizards.functions.core.Messages;
import eu.esdihumboldt.hale.ui.common.definition.DefinitionLabelFactory;
import eu.esdihumboldt.hale.ui.model.functions.AbstractSingleComposedCellWizardPage;
import eu.esdihumboldt.specification.cst.align.ext.IParameter;

/**
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class UserDefinedFunctionWizardPage extends
		AbstractSingleComposedCellWizardPage {
	
	private String predefinedUdfName = null;
	private List<IParameter> predefinedParameters = null;
	
	/**
	 * The key represents the name of the template, the value list represents 
	 * the name of each of the possible parameters.
	 */
	private final Map<String, List<EditableParameter>> udfTemplates = 
		new TreeMap<String, List<EditableParameter>>();
	
	private ComboViewer templates;
	private Text udfName;
	private Table table;
	private TableViewer tableViewer;
	
	private Button saveButton;
	private Button removeButton;
	
	private final Image addImage = 
		CoreFunctionWizardsPlugin.getImageDescriptor("icons/add.gif").createImage(); //$NON-NLS-1$
	private final Image removeImage = 
		CoreFunctionWizardsPlugin.getImageDescriptor("icons/remove.gif").createImage(); //$NON-NLS-1$
	private final Image saveImage = 
		CoreFunctionWizardsPlugin.getImageDescriptor("icons/save.gif").createImage(); //$NON-NLS-1$

	private final String udfSeparator = "<udfFunction>"; //$NON-NLS-1$
	private final String nameParameterSeparator = "<nameParameter>"; //$NON-NLS-1$
	private final String parameterSeparator = "<parameter>"; //$NON-NLS-1$

	/**
	 * Creates the page with a defined pageName.
	 * @param pageName the name for the page to use.
	 */
	public UserDefinedFunctionWizardPage(String pageName) {
		super(pageName);
		super.setTitle(pageName);
		super.setDescription(Messages.UserDefinedFunctionWizardPage_6);
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// load already defined UDF templates
		IPreferenceStore preferences = CoreFunctionWizardsPlugin.plugin.getPreferenceStore();
		String storedTemplates = preferences.getString("eu.esdihumboldt.udfPreferences"); //$NON-NLS-1$
		if (storedTemplates != null && !storedTemplates.equals("")) { //$NON-NLS-1$
			String[] separatedTemplates = storedTemplates.split(udfSeparator);
			for (String tpl : separatedTemplates) {
				// separate name from parameters
				String[] nameParameters = tpl.split(nameParameterSeparator);
				if (nameParameters.length == 2) {
					List<EditableParameter> parameters = new ArrayList<EditableParameter>();
					for (String key : nameParameters[1].split(parameterSeparator)) {
						parameters.add(new EditableParameter(key, "")); //$NON-NLS-1$
					}
					this.udfTemplates.put(nameParameters[0], parameters);
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
		entityInfoGroup.setText(Messages.UserDefinedFunctionWizardPage_10);
		entityInfoGroup.setLayout(new GridLayout(2, false));
		GridData selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		entityInfoGroup.setLayoutData(selectionAreaGD);
		entityInfoGroup.setSize(entityInfoGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		entityInfoGroup.setFont(page.getFont());
		
		final Label sourceEntitiesLabel = new Label(entityInfoGroup, SWT.NONE);
		sourceEntitiesLabel.setText(Messages.UserDefinedFunctionWizardPage_11);
		
		Control sourceLabel = dlf.createLabel(entityInfoGroup, 
				getParent().getFirstSourceItem().getDefinition(), true);
		sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		
		final Label targetEntitiesLabel = new Label(entityInfoGroup, SWT.NONE);
		targetEntitiesLabel.setText(Messages.UserDefinedFunctionWizardPage_12);
		
		Control targetLabel = dlf.createLabel(entityInfoGroup, 
				getParent().getFirstTargetItem().getDefinition(), true);
		targetLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.LEFT, false, false));
		
		// Group 1: Selection of a UDF template (are stored using eclipse preferences store)
		Group selectUdfTemplateGroup = new Group(page, SWT.NONE);
		selectUdfTemplateGroup.setText(Messages.UserDefinedFunctionWizardPage_13);
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
					tableViewer.setInput(udfTemplates.get(key));
					table.setEnabled(true);
					removeButton.setEnabled(true);
				}
			}
			
		});
		
		// add UDF template
		Button addButton = new Button(selectUdfTemplateGroup, SWT.PUSH);
		addButton.setImage(addImage);
		addButton.setToolTipText(Messages.UserDefinedFunctionWizardPage_14);
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				udfName.setText(""); //$NON-NLS-1$
				table.setEnabled(true);
				List<EditableParameter> emptyParams = new ArrayList<EditableParameter>();
				for (int i = 0; i < 20; i++) {
					emptyParams.add(new EditableParameter("", "")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				tableViewer.setInput(emptyParams);
				udfName.setEnabled(true);
				saveButton.setEnabled(true);
			}
			
		});
		
		// save current UDF template
		this.saveButton = new Button(selectUdfTemplateGroup, SWT.PUSH);
		this.saveButton.setImage(saveImage);
		this.saveButton.setEnabled(false);
		this.saveButton.setToolTipText(Messages.UserDefinedFunctionWizardPage_18);
		this.saveButton.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (udfName != null && udfName.getText() != null 
						&& !udfName.getText().equals("")) { //$NON-NLS-1$
					udfTemplates.put(udfName.getText(), 
							(List<EditableParameter>) tableViewer.getInput());
					templates.setInput(udfTemplates.keySet());
				}
			}
			
		});
		
		// remove UDF template
		this.removeButton = new Button(selectUdfTemplateGroup, SWT.PUSH);
		this.removeButton.setImage(removeImage);
		this.removeButton.setEnabled(false);
		this.removeButton.setToolTipText(Messages.UserDefinedFunctionWizardPage_20);
		this.removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				udfName.setEnabled(false);
				udfName.setText(""); //$NON-NLS-1$
				udfTemplates.remove(((StructuredSelection)templates.getSelection()).getFirstElement().toString());
				templates.setInput(udfTemplates.keySet());
				tableViewer.setInput(new ArrayList<EditableParameter>());
			}
			
		});
		
		// Group 2: Parameters of the UDF that has been selected
		Group udfParametersGroup = new Group(page, SWT.NONE);
		udfParametersGroup.setText(Messages.UserDefinedFunctionWizardPage_22);
		udfParametersGroup.setLayout(new GridLayout());
		selectionAreaGD = new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL);
		selectionAreaGD.grabExcessHorizontalSpace = true;
		udfParametersGroup.setLayoutData(selectionAreaGD);
		udfParametersGroup.setSize(selectUdfTemplateGroup.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		udfParametersGroup.setFont(page.getFont());
		
		final Label udfNameLabel = new Label(udfParametersGroup, SWT.NONE);
		udfNameLabel.setText(Messages.UserDefinedFunctionWizardPage_23);
		udfNameLabel.setToolTipText(Messages.UserDefinedFunctionWizardPage_24);
		
		this.udfName = new Text (udfParametersGroup, SWT.BORDER | SWT.SINGLE);
		this.udfName.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		this.udfName.setEnabled(false);
		
		final Label udfParametersLabel = new Label(udfParametersGroup, SWT.NONE);
		udfParametersLabel.setText(Messages.UserDefinedFunctionWizardPage_25);
		udfParametersLabel.setToolTipText(Messages.UserDefinedFunctionWizardPage_26);
		
		this.table = new Table (udfParametersGroup, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		this.table.setLinesVisible (true);
		this.table.setHeaderVisible (true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);
		this.table.setEnabled(false);
		
		this.tableViewer = new TableViewer(this.table);
		this.tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		TableViewerColumn viewerNameColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		viewerNameColumn.getColumn().setText(Messages.UserDefinedFunctionWizardPage_27);
		viewerNameColumn.getColumn().setWidth(125);
		viewerNameColumn.setLabelProvider(new CellLabelProvider() {
		    @Override
		    public void update(ViewerCell cell) {
		        cell.setText(((EditableParameter) cell.getElement()).getName());
		    }
		});
		viewerNameColumn.setEditingSupport(new EditingSupport(tableViewer) {
		    protected boolean canEdit(Object element) {
		        return true;
		    }
		    protected CellEditor getCellEditor(Object element) {
		        return new TextCellEditor(tableViewer.getTable());
		    }
		    protected Object getValue(Object element) {
		        return ((EditableParameter) element).getName();
		    }
		    protected void setValue(Object element, Object value) {
		    	((EditableParameter) element).setName(String.valueOf(value));
		        tableViewer.refresh(element);
		    }
		});
		
		TableViewerColumn viewerValueColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		viewerValueColumn.getColumn().setText(Messages.UserDefinedFunctionWizardPage_28);
		viewerValueColumn.getColumn().setWidth(340);
		viewerValueColumn.setLabelProvider(new CellLabelProvider() {
		    @Override
		    public void update(ViewerCell cell) {
		        cell.setText(((EditableParameter) cell.getElement()).getValue());
		    }
		});
		viewerValueColumn.setEditingSupport(new EditingSupport(tableViewer) {
		    protected boolean canEdit(Object element) {
		        return true;
		    }
		    protected CellEditor getCellEditor(Object element) {
		        return new TextCellEditor(tableViewer.getTable());
		    }
		    protected Object getValue(Object element) {
		        return ((EditableParameter) element).getValue();
		    }
		    protected void setValue(Object element, Object value) {
		    	((EditableParameter) element).setValue(String.valueOf(value));
		        tableViewer.refresh(element);
		    }
		});
		
		if (this.predefinedParameters != null && this.predefinedUdfName != null) {
			this.udfName.setText(this.predefinedUdfName);
			this.udfName.setEnabled(true);
			List<EditableParameter> input = new ArrayList<EditableParameter>();
			for (IParameter param : this.predefinedParameters) {
				input.add(new EditableParameter(param.getName(), param.getValue()));
			}
			this.tableViewer.setInput(input);
			this.table.setEnabled(true);
		}
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
			for (EditableParameter param : this.udfTemplates.get(key)) {
				if (!first) {
					result.append(this.parameterSeparator);
				}
				else {
					first = false;
				}
				result.append(param.getName());
			}
		}
		return result.toString();
	}
	
	/**
	 * @return the name of the configured Udf.
	 */
	public String getUdfName() {
		return this.udfName.getText();
	}
	
	@SuppressWarnings("unchecked")
	public List<IParameter> getUdfParameters() {
		List<IParameter> result = new ArrayList<IParameter>();
		for (EditableParameter input: (List<EditableParameter>) this.tableViewer.getInput()) {
			result.add(input.asParameter());
		}
		return result;
	}
	

	public void setInitialConfiguration(String udfName,
			List<IParameter> parameters) {
		this.predefinedParameters = parameters;
		this.predefinedUdfName = udfName.replace("UserDefinedFunction.", ""); //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	protected class EditableParameter {
		
		private String name;
		
		private String value;

		public EditableParameter(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return this.name;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public Parameter asParameter() {
			return new Parameter(this.name, this.value);
		}
		
	}

}
