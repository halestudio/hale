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
package eu.esdihumboldt.hale.rcp.wizards.functions.literal;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.wizards.functions.AbstractSingleCellWizardPage;

/**
 * This {@link WizardPage} is used to define a renaming mapping.
 * 
 * @author Anna Pitaev
 * @version {$Id}
 */
public class RenamingFunctionWizardMainPage 
		extends AbstractSingleCellWizardPage {
	
	/**
	 * Instance mapping types
	 */
	public enum InstanceMappingType {
		/** normal 1:1 */
		NORMAL,
		/** split 1:n */
		SPLIT,
		/** merge n:1 */
		MERGE
	}
	
	private InstanceMappingType type = InstanceMappingType.NORMAL;
	
	private String initialCondition = null;
	
	private Text condition; 
	
	private ListViewer varList;

	/**
	 * Constructor
	 * 
	 * @param pageName the page name
	 * @param title the page title
	 */
	protected RenamingFunctionWizardMainPage(String pageName, String title) {
		super(pageName, title, (ImageDescriptor) null);
		setTitle(pageName);
		//setDescription("Enter parameters to adopt the source FeatureType to the target Naming Convention.");
	}

	/**
	 * The parent methods where all controls are created for this
	 * {@link WizardPage}.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		super.initializeDialogUnits(parent);
		// create a composite to hold the widgets
		Composite page = new Composite(parent, SWT.NULL);
		// create layout for this wizard page
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginLeft = 0;
		gl.marginTop = 20;
		gl.marginRight = 70;
		page.setLayout(gl);

		// source area
		Label sourceLabel = new Label(page, SWT.NONE);
		sourceLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		sourceLabel.setText("Source " + ((getParent().getSourceItem().isType())?("type"):("attribute")));
		
		Text sourceName = new Text(page, SWT.BORDER);
		sourceName.setText(getParent().getSourceItem().getName().getLocalPart());
		sourceName.setEnabled(false);
		sourceName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// target area
		Label targetLabel = new Label(page, SWT.NONE);
		targetLabel.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		targetLabel.setText("Target " + ((getParent().getTargetItem().isType())?("type"):("attribute")));
		
		Text targetName = new Text(page, SWT.BORDER);
		targetName.setText(getParent().getTargetItem().getName().getLocalPart());
		targetName.setEnabled(false);
		targetName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if (getParent().getSourceItem().isFeatureType() && getParent().getTargetItem().isFeatureType()) {
			new Composite(page, SWT.NONE);
			
			// instance mapping
			Group group = new Group(page, SWT.NONE);
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			group.setLayout(new GridLayout(2, false));
			group.setText("Instance mapping");
			
			Button normal = new Button(group, SWT.RADIO);
			normal.setText("1:1");
			normal.setSelection(type == InstanceMappingType.NORMAL);
			normal.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
			normal.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					type = InstanceMappingType.NORMAL;
					update();
				}
				
			});
			
			Button split = new Button(group, SWT.RADIO);
			split.setText("1:n (split)");
			split.setSelection(type == InstanceMappingType.SPLIT);
			split.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
			split.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					type = InstanceMappingType.SPLIT;
					update();
				}
				
			});
			
			Button merge = new Button(group, SWT.RADIO);
			merge.setText("n:1 (merge)");
			merge.setSelection(type == InstanceMappingType.MERGE);
			merge.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
			merge.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					type = InstanceMappingType.MERGE;
					update();
				}
				
			});
			
			// condition
			Label labelCondition = new Label(group, SWT.NONE);
			labelCondition.setText("Condition:");
			labelCondition.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
			
			condition = new Text(group, SWT.BORDER);
			if (initialCondition != null) {
				condition.setText(initialCondition);
			}
			condition.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			
			// variables
			new Label(group, SWT.NONE);
			
			Label label = new Label(group, SWT.NONE);
			label.setText("Available variables (double click to insert)");
			label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
			
			Set<String> variables = new TreeSet<String>();
			if (getParent().getSourceItem().hasChildren()) {
				for (SchemaItem child : getParent().getSourceItem().getChildren()) {
					variables.add(child.getName().getLocalPart());
				}
			}
			
			new Label(group, SWT.NONE);
			
			varList = new ListViewer(group);
			varList.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			varList.setContentProvider(new ArrayContentProvider());
			varList.setInput(variables);
			varList.getList().addMouseListener(new MouseAdapter() {

				/**
				 * @see MouseAdapter#mouseDoubleClick(MouseEvent)
				 */
				@Override
				public void mouseDoubleClick(MouseEvent e) {
					int index = varList.getList().getSelectionIndex();
					if (index >= 0) {
						String var = varList.getList().getItem(index);
						condition.insert(var);
						condition.setFocus();
					}
				}
				
			});
			
			update();
		}
		
		super.setControl(page);
	}

	private void update() {
		if (condition != null) {
			condition.setEnabled(type != InstanceMappingType.NORMAL);
		}
		if (varList != null) {
			varList.getControl().setEnabled(type != InstanceMappingType.NORMAL);
		}
	}

	/**
	 * @return the type
	 */
	public InstanceMappingType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(InstanceMappingType type) {
		this.type = type;
	}

	/**
	 * @return the condition
	 */
	public String getCondition() {
		return condition.getText();
	}

	/**
	 * @param condition the condition to set
	 */
	public void setInitialCondition(String condition) {
		this.initialCondition = condition;
	}

}
