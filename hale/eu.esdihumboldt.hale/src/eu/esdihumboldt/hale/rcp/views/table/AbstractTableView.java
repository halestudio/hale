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
package eu.esdihumboldt.hale.rcp.views.table;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.rcp.views.table.filter.FeatureSelectionListener;
import eu.esdihumboldt.hale.rcp.views.table.filter.FeatureSelector;
import eu.esdihumboldt.hale.rcp.views.table.tree.FeatureTreeViewer;

/**
 * Table view that shows information about certain features
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractTableView extends ViewPart {

	/**
	 * The feature tree viewer
	 */
	private FeatureTreeViewer tree;
	
	private Composite selectorComposite;
	
	private FeatureSelector featureSelector;
	
	private Control selectorControl;
	
	/**
	 * Creates a table view
	 * 
	 * @param featureSelector the feature selector
	 */
	public AbstractTableView(FeatureSelector featureSelector) {
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
		tree = new FeatureTreeViewer(treeComposite);
		
		// selector
		setFeatureSelector(featureSelector);
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
		// ignore
	}

	/**
	 * @return the featureSelector
	 */
	public FeatureSelector getFeatureSelector() {
		return featureSelector;
	}

	/**
	 * @param featureSelector the featureSelector to set
	 */
	public void setFeatureSelector(FeatureSelector featureSelector) {
		this.featureSelector = featureSelector;
		
		// remove old control
		if (selectorControl != null) {
			selectorControl.dispose();
		}
		
		// create new control
		selectorControl = featureSelector.createControl(selectorComposite);
		selectorControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		// re-layout
		selectorComposite.getParent().getParent().layout(true, true);
		
		// add listener
		featureSelector.addSelectionListener(new FeatureSelectionListener() {
			
			@Override
			public void selectionChanged(FeatureType type, Iterable<Feature> selection) {
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
	protected void onSelectionChange(Iterable<Feature> selection) {
		// do nothing
	}

}
