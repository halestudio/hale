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
package eu.esdihumboldt.hale.ui.function.common;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Dialog for selecting an {@link EntityDefinition}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class EntityDialog extends Dialog {
	
	private EntityDefinition entity;
	
	private TreeViewer viewer;
	
	/**
	 * The schema space
	 */
	protected final SchemaSpaceID ssid;
	
	private final String title;
	
	/**
	 * Constructor
	 * 
	 * @param parentShell the parent shell
	 * @param ssid the schema space
	 * @param title the dialog title
	 */
	public EntityDialog(Shell parentShell, SchemaSpaceID ssid, String title) {
		super(parentShell);
		
		this.ssid = ssid;
		this.title = title;
	}
	
	/**
	 * @see Dialog#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		
		updateState();
		
		return control;
	}

	/**
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(title);
	}

	/**
	 * @see Dialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.widthHint = 400;
		data.minimumHeight = 200;
		page.setLayoutData(data);
		
		GridLayout pageLayout = new GridLayout(1, false);
		pageLayout.marginLeft = 0;
		pageLayout.marginTop = 0;
		pageLayout.marginLeft = 0;
		pageLayout.marginBottom = 0;
		page.setLayout(pageLayout);
		
		// create viewer
		viewer = new TreeViewer(page);
		viewer.setComparator(new DefinitionComparator());
		setupViewer(viewer);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().
				grab(true, true).create());
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateState();
			}
		});
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
			}
		});
		
		return page;
	}
	
	private void updateState() {
		Button ok = getButton(IDialogConstants.OK_ID);
		
		if (ok != null) {
			boolean selected = !viewer.getSelection().isEmpty();
			ok.setEnabled(selected);
		}
	}
	
	/**
	 * Setup the tree viewer with label provider, content provider and input. 
	 * @param viewer the tree viewer
	 */
	protected abstract void setupViewer(TreeViewer viewer);

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		entity = getEntityFromSelection(viewer.getSelection());
				
		super.okPressed();
	}

	/**
	 * Retrieve the selected entity from the given selection
	 * @param selection the selection
	 * @return the selected entity or <code>null</code>
	 */
	protected abstract EntityDefinition getEntityFromSelection(ISelection selection);

	/**
	 * @see Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		entity = null;
		
		super.cancelPressed();
	}

	/**
	 * @return the entity
	 */
	public EntityDefinition getEntity() {
		return entity;
	}

}
