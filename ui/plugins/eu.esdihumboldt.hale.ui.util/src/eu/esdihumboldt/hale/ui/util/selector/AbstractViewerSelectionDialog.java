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
package eu.esdihumboldt.hale.ui.util.selector;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
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
import org.eclipse.swt.widgets.ToolBar;

/**
 * Abstract selection dialog based on a structured viewer.
 * 
 * @author Simon Templer
 * @param <T> the type of object that can be selected in the dialog
 * @param <V> the type of the viewer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class AbstractViewerSelectionDialog<T, V extends StructuredViewer>
		extends TrayDialog {

	private static final int NONE_ID = IDialogConstants.CLIENT_ID + 1;

	private T selected;

	private V viewer;

	private final String title;

	private ViewerFilter[] filters;

	private final T initialSelection;

	private final boolean allowNone;

	private int widthHint;

	private int heightHint;

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *            possible), may be <code>null</code>
	 */
	public AbstractViewerSelectionDialog(Shell parentShell, String title, T initialSelection) {
		this(parentShell, title, initialSelection, true, 400, 400);
	}

	/**
	 * Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param title the dialog title
	 * @param initialSelection the entity definition to select initially (if
	 *            possible), may be <code>null</code>
	 * @param allowNone if selecting the None button is allowed
	 * @param widthHint the width hint for the dialog
	 * @param heightHint the height hint for the dialog
	 */
	public AbstractViewerSelectionDialog(Shell parentShell, String title, T initialSelection,
			boolean allowNone, int widthHint, int heightHint) {
		super(parentShell);
		this.title = title;
		this.initialSelection = initialSelection;
		this.allowNone = allowNone;
		this.widthHint = widthHint;
		this.heightHint = heightHint;
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
		data.widthHint = widthHint;
//		data.minimumHeight = 200;
		data.heightHint = heightHint;
		page.setLayoutData(data);

		GridLayout pageLayout = new GridLayout(1, false);
		pageLayout.marginLeft = 0;
		pageLayout.marginTop = 0;
		pageLayout.marginLeft = 0;
		pageLayout.marginBottom = 0;
		page.setLayout(pageLayout);

		// create tool bar first so that it is on top
		ToolBarManager manager = new ToolBarManager(SWT.FLAT | SWT.WRAP);
		ToolBar toolbar = manager.createControl(page);
		toolbar.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));

		viewer = createViewer(page);
		setupViewer(viewer, initialSelection);

		// fill it after viewer creation, so that the viewer can be referenced
		// in there
		addToolBarActions(manager);
		manager.update(false);
		// dispose the tool bar again, if it is empty
		if (manager.getSize() == 0)
			toolbar.dispose();

		if (!(viewer.getControl().getLayoutData() instanceof GridData)) {
			viewer.getControl()
					.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		}

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
	 * Create the tree viewer.
	 * 
	 * @param parent the parent composite
	 * @return the tree viewer
	 */
	protected abstract V createViewer(Composite parent);

	/**
	 * Add tool bar actions to the entity dialog tool bar.
	 * 
	 * @param manager the tool bar manager
	 */
	protected void addToolBarActions(ToolBarManager manager) {
		// add nothing
	}

	/**
	 * @see Dialog#createButtonsForButtonBar(Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		if (allowNone) {
			createButton(parent, NONE_ID, "None", false);
		}
	}

	/**
	 * @see Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case NONE_ID:
			this.selected = null;
			setReturnCode(OK);
			close();
			break;
		default:
			super.buttonPressed(buttonId);
		}
	}

	/**
	 * Determines if the given object matches the given filters.
	 * 
	 * @param viewer the viewer
	 * @param filters the viewer filters
	 * @param candidate the object to test
	 * @return if the object is accepted by all filters
	 */
	public static boolean acceptObject(Viewer viewer, ViewerFilter[] filters, Object candidate) {
		if (filters == null)
			return true;

		for (ViewerFilter filter : filters)
			if (!filter.select(viewer, null, candidate))
				return false;
		return true;
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
					if (!acceptObject(viewer, filters,
							((IStructuredSelection) selection).getFirstElement()))
						valid = false;
				}
				ok.setEnabled(valid);
			}
		}
	}

	/**
	 * Setup the tree viewer with label provider, content provider and input.
	 * Don't set any viewer filters as they will be overridden by those provided
	 * through {@link #setFilters(ViewerFilter[])}.
	 * 
	 * @param viewer the tree viewer
	 * @param initialSelection the object to select (if possible), may be
	 *            <code>null</code>
	 */
	protected abstract void setupViewer(V viewer, T initialSelection);

	/**
	 * @see Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		selected = getObjectFromSelection(viewer.getSelection());

		super.okPressed();
	}

	/**
	 * Retrieve the selected entity from the given selection
	 * 
	 * @param selection the selection
	 * @return the selected entity or <code>null</code>
	 */
	protected abstract T getObjectFromSelection(ISelection selection);

	/**
	 * @see Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		selected = null;

		super.cancelPressed();
	}

	/**
	 * Get the selected object.
	 * 
	 * @return the object or <code>null</code>
	 */
	public T getObject() {
		return selected;
	}

	/**
	 * Set the internal selection.
	 * 
	 * @param selected the selection
	 */
	protected void internalSetSelected(T selected) {
		this.selected = selected;
	}

	/**
	 * Set the viewer filters
	 * 
	 * @param filters the filters
	 */
	public void setFilters(ViewerFilter[] filters) {
		this.filters = filters;
	}

	/**
	 * @return the filters
	 */
	public ViewerFilter[] getFilters() {
		return filters;
	}

	/**
	 * @return the viewer
	 */
	protected V getViewer() {
		return viewer;
	}

}
