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
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.rcp.views.table.filter.FeatureSelector;
import eu.esdihumboldt.hale.rcp.views.table.filter.FeatureSelector.FeatureSelectionListener;
import eu.esdihumboldt.hale.rcp.views.table.tree.FeatureTreeViewer;

/**
 * Table view that shows information about certain features
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TableView extends ViewPart {

	/**
	 * The view id
	 */
	public static final String ID = "eu.esdihumboldt.hale.rcp.views.TableView";
	
	/**
	 * The feature tree viewer
	 */
	private FeatureTreeViewer tree;

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
		FeatureSelector selector = new FeatureSelector(page);
		selector.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		// tree composite
		Composite treeComposite = new Composite(page, SWT.NONE);
		treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		// tree column layout
		TreeColumnLayout layout = new TreeColumnLayout(); 
		treeComposite.setLayout(layout);
		
		// tree viewer
		tree = new FeatureTreeViewer(treeComposite);
		
		selector.addSelectionListener(new FeatureSelectionListener() {
			
			@Override
			public void selectionChanged(FeatureType type, Iterable<Feature> selection) {
				tree.setInput(type, selection);
			}
		});
	}

	/**
	 * @see WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// ignore
	}

}
