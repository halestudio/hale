/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.views.data;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceSelectionListener;
import eu.esdihumboldt.hale.ui.views.data.internal.filter.InstanceSelector;
import eu.esdihumboldt.hale.ui.views.data.internal.tree.DefinitionInstanceTreeViewer;
import eu.esdihumboldt.hale.ui.views.properties.PropertiesViewPart;

/**
 * Table view that shows information about certain features
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractDataView extends PropertiesViewPart {

	/**
	 * The feature tree viewer
	 */
	private DefinitionInstanceTreeViewer tree;
	
	private Composite selectorComposite;
	
	private InstanceSelector featureSelector;
	
	private Control selectorControl;
	
	/**
	 * Creates a table view
	 * 
	 * @param featureSelector the feature selector
	 */
	public AbstractDataView(InstanceSelector featureSelector) {
		super();
		
		this.featureSelector = featureSelector;
	}

	/**
	 * @see WorkbenchPart#createPartControl(Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 300;
		page.setLayoutData(data);
		page.setLayout(new GridLayout(1, false));
		
		// bar composite
		Composite bar = new Composite(page, SWT.NONE);
		bar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		bar.setLayout(gridLayout);
		
		// custom control composite
		Composite custom = new Composite(bar, SWT.NONE);
		custom.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		provideCustomControls(custom);
		
		// selector composite
		selectorComposite = new Composite(bar, SWT.NONE);
		selectorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		selectorComposite.setLayout(gridLayout);
		
		// tree composite
		Composite treeComposite = new Composite(page, SWT.NONE);
		treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// tree column layout
		TreeColumnLayout layout = new TreeColumnLayout(); 
		treeComposite.setLayout(layout);
		
		// tree viewer
		tree = new DefinitionInstanceTreeViewer(treeComposite); //new FeatureTreeViewer(treeComposite);
		
		// selector
		setInstanceSelector(featureSelector);
		
		getSite().setSelectionProvider(tree.getTreeViewer());
	}

	/**
	 * Add custom controls. Override this method to add custom controls
	 * 
	 * @param parent the parent composite
	 */
	protected void provideCustomControls(Composite parent) {
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		// empty composite trick: exclude
		gd.exclude = true;
		parent.setLayoutData(gd);
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		tree.getTreeViewer().getControl().setFocus();
	}

	/**
	 * @return the featureSelector
	 */
	public InstanceSelector getFeatureSelector() {
		return featureSelector;
	}

	/**
	 * @param instanceSelector the instance selector to set
	 */
	public void setInstanceSelector(InstanceSelector instanceSelector) {
		this.featureSelector = instanceSelector;
		
		// remove old control
		if (selectorControl != null) {
			selectorControl.dispose();
		}
		
		// create new control
		selectorControl = instanceSelector.createControl(selectorComposite);
		selectorControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		// re-layout
		selectorComposite.getParent().getParent().layout(true, true);
		
		// add listener
		instanceSelector.addSelectionListener(new InstanceSelectionListener() {
			
			@Override
			public void selectionChanged(TypeDefinition type, Iterable<Instance> selection) {
				tree.setInput(type, selection);
				onSelectionChange(selection);
			}
		});
	}
	
	/**
	 * Called when the selection has changed
	 * 
	 * @param selection the current selection 
	 */
	protected void onSelectionChange(Iterable<Instance> selection) {
		// do nothing
	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		selectorControl.dispose();
	}

}
