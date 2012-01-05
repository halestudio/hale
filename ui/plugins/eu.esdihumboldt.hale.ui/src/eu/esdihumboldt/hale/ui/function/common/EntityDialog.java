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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathFilteredTree;

/**
 * Dialog for selecting an {@link EntityDefinition}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class EntityDialog extends Dialog {
	
	private static final int NONE_ID = IDialogConstants.CLIENT_ID + 1;

	private EntityDefinition entity;
	
	private TreeViewer viewer;
	
	/**
	 * The schema space
	 */
	protected final SchemaSpaceID ssid;
	
	private final String title;

	private ViewerFilter[] filters;

	private final EntityDefinition initialSelection;
	
	/**
	 * Constructor
	 * 
	 * @param parentShell the parent shell
	 * @param ssid the schema space
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *   possible), may be <code>null</code>
	 */
	public EntityDialog(Shell parentShell, SchemaSpaceID ssid, String title,
			EntityDefinition initialSelection) {
		super(parentShell);
		
		this.ssid = ssid;
		this.title = title;
		this.initialSelection = initialSelection;
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
		SchemaPatternFilter patternFilter = new SchemaPatternFilter() {
			/**
			 * @see eu.esdihumboldt.hale.ui.common.definition.viewer.SchemaPatternFilter#matches(org.eclipse.jface.viewers.Viewer, java.lang.Object)
			 */
			@Override
			protected boolean matches(Viewer viewer, Object element) {
				boolean superMatches = super.matches(viewer, element);
				if (!superMatches)
					return false;
				return EntitySelector.acceptObject(viewer, filters, ((TreePath)element).getLastSegment());
			}
		};
		patternFilter.setUseEarlyReturnIfMatcherIsNull(false);
		patternFilter.setIncludeLeadingWildcard(true);
		FilteredTree tree = new TreePathFilteredTree(page, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter, true);
		viewer = tree.getViewer();
		viewer.setComparator(new DefinitionComparator());
		setupViewer(viewer, initialSelection);
		
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
				if (getButton(IDialogConstants.OK_ID).isEnabled())
					okPressed();
			}
		});
		
		updateState();
		
		return page;
	}
	
	/**
	 * @see Dialog#createButtonsForButtonBar(Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		
		createButton(parent, NONE_ID, "None", //$NON-NLS-1$
				false);
	}

	/**
	 * @see Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case NONE_ID:
			this.entity = null;
			setReturnCode(OK);
			close();
			break;
		default:
			super.buttonPressed(buttonId);
		}
	}

	private void updateState() {
		Button ok = getButton(IDialogConstants.OK_ID);
		
		if (ok != null) {
			ISelection selection = viewer.getSelection();
			if (selection.isEmpty())
				ok.setEnabled(false);
			else {
				boolean valid = true;
				if (selection instanceof IStructuredSelection) {
					if (!EntitySelector.acceptObject(viewer, filters, ((IStructuredSelection) selection).getFirstElement()))
						valid = false;
				}
				ok.setEnabled(valid);
			}
		}
	}
	
	/**
	 * Setup the tree viewer with label provider, content provider and input.
	 * Don't set any viewer filters as they will be overridden by those 
	 * provided through {@link #setFilters(ViewerFilter[])}. 
	 * @param viewer the tree viewer
	 * @param initialSelection the entity definition to select (if possible),
	 *   may be <code>null</code> 
	 */
	protected abstract void setupViewer(TreeViewer viewer, 
			EntityDefinition initialSelection);

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
	 * Get the selected entity definition
	 * @return the entity definition or <code>null</code>
	 */
	public EntityDefinition getEntity() {
		return entity;
	}

	/**
	 * Set the viewer filters
	 * @param filters the filters
	 */
	public void setFilters(ViewerFilter[] filters) {
		this.filters = filters;
	}

}
