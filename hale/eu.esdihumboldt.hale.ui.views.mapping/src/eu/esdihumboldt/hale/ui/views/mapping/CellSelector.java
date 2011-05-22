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
package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IDisposable;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.mapping.helper.EntityHelper;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardContribution;
import eu.esdihumboldt.hale.ui.model.mapping.CellInfo;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.hale.ui.selection.CellSelection;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaServiceAdapter;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService.SchemaType;
import eu.esdihumboldt.hale.ui.service.schemaitem.SchemaItemService;
import eu.esdihumboldt.hale.ui.views.mapping.internal.MappingViewPlugin;
import eu.esdihumboldt.hale.ui.views.mapping.internal.Messages;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Control for selecting a cell, reacts on {@link SchemaSelection}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CellSelector implements ISelectionListener, IDisposable, ISelectionProvider {
	
	/**
	 * Contribution for local selection
	 */
	private class CellFunctionContribution extends FunctionWizardContribution {

		public CellFunctionContribution() {
			super(true);
		}

		/**
		 * @see FunctionWizardContribution#getSelection()
		 */
		@Override
		protected ISelection getSelection() {
			return cellSelection;
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(CellSelector.class);
	
	private final ComboViewer viewer;
	
	private final AlignmentService alignmentService;
	
	/**
	 * The last schema selection
	 */
	private ISelection lastSelection = null;
	
	private ISelection lastViewSelection = null;
	
	private CellInfo lastSelected = null;
	
	/**
	 * The currently selected cell
	 */
	private CellInfo selected = null;
	
	/**
	 * The current cell selection
	 */
	private ISelection cellSelection;
	
	private final Composite page;
	
	private final Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();
	
	private final Image prevImage;
	
	private final Image nextImage;
	
	private final Image editImage;
	
	private final Image synchImage;
	
	private final Image deleteImage;
	
	private final Button prevButton;
	
	private final Button nextButton;
	
	private final ISelectionService selectionService;

	private final HaleServiceListener alignmentListener;
	
	private boolean useViewSelection = false;

	private final SchemaServiceAdapter itemListener;
	
	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 * @param selectionService the selection service
	 */
	public CellSelector(Composite parent, ISelectionService selectionService) {
		super();
		
		this.selectionService = selectionService;
		
		alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		// composite
		page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(6, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 2;
		page.setLayout(layout);
		
		// synch button
		Button synchButton = new Button(page, SWT.TOGGLE);
		synchButton.setSelection(useViewSelection);
		synchButton.setToolTipText(Messages.CellSelector_SynchButtonToolTipText);
		synchImage = MappingViewPlugin.getImageDescriptor("icons/refresh.gif").createImage(); //$NON-NLS-1$
		synchButton.setImage(synchImage);
		synchButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				useViewSelection = !useViewSelection;
				updateNow();
			}
		});
		
		// navigation buttons
		prevButton = new Button(page, SWT.PUSH);
		prevButton.setEnabled(false);
		prevButton.setToolTipText(Messages.CellSelector_PrevButtonToolTipText);
		prevImage = MappingViewPlugin.getImageDescriptor("icons/backward_nav.gif").createImage(); //$NON-NLS-1$
		prevButton.setImage(prevImage);
		prevButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object element = viewer.getElementAt(viewer.getCombo().getSelectionIndex() - 1);
				viewer.setSelection(new StructuredSelection(element));
			}
			
		});
		
		nextButton = new Button(page, SWT.PUSH);
		nextButton.setEnabled(false);
		nextButton.setToolTipText(Messages.CellSelector_NextButtonToolTipText);
		nextImage = MappingViewPlugin.getImageDescriptor("icons/forward_nav.gif").createImage(); //$NON-NLS-1$
		nextButton.setImage(nextImage);
		nextButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object element = viewer.getElementAt(viewer.getCombo().getSelectionIndex() + 1);
				viewer.setSelection(new StructuredSelection(element));
			}
			
		});
		
		// combo box
		Combo combo = new Combo(page, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		viewer = new ComboViewer(combo);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSelection(StructuredSelection.EMPTY);
		viewer.setLabelProvider(new LabelProvider() {

			/**
			 * @see LabelProvider#getText(Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof CellInfo) {
					CellInfo cell = (CellInfo) element;
					return EntityHelper.getShortName(cell.getCell().getEntity1()) +
						" - " + EntityHelper.getShortName(cell.getCell().getEntity2()); //$NON-NLS-1$
				}
				else {
					return super.getText(element);
				}
			}
			
		});
		
		// edit buttons
		final Button editButton = new Button(page, SWT.PUSH);
		editButton.setToolTipText(Messages.CellSelector_EditButtonToolTipText);
		editImage = MappingViewPlugin.getImageDescriptor("icons/editor_area.gif").createImage(); //$NON-NLS-1$
		editButton.setImage(editImage);
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		final IContributionItem editContribution = new CellFunctionContribution();
		manager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(editContribution);
			}
			
		});
		final Menu editMenu = manager.createContextMenu(editButton);
		editButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editMenu.setLocation(editButton.toDisplay(0, editButton.getSize().y));
				editMenu.setVisible(true);
			}
			
		});
		
		final Button deleteButton = new Button(page, SWT.PUSH);
		deleteButton.setToolTipText(Messages.CellSelector_DeleteButtonToolTipText);
		deleteImage = MappingViewPlugin.getImageDescriptor("icons/delete_edit.gif").createImage(); //$NON-NLS-1$
		deleteButton.setImage(deleteImage);
		deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see SelectionAdapter#widgetSelected(SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected != null) {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							if (MessageDialog.openQuestion(
									page.getShell(), 
									Messages.CellSelector_ConfirmCellTitle, 
									Messages.CellSelector_ConfirmCellText)) {
								alignmentService.removeCell(selected.getCell());
							}
						}
					});
				}
			}
		});
		
		
		// edit buttons state
		addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean enabled = !event.getSelection().isEmpty();
				editButton.setEnabled(enabled);
				deleteButton.setEnabled(enabled);
			}
			
		});
		
		// initial selection event
		fireCellSelectionChange(null);
		
		// react on cell selection changes
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				
				if (selection == null || selection.isEmpty()) {
					fireCellSelectionChange(null);
				}
				else {
					CellInfo selectedCell = (CellInfo) ((StructuredSelection) selection).getFirstElement();
					lastSelected = selectedCell;
					fireCellSelectionChange(selectedCell);
				}
				
				// navigation buttons
				Combo combo = viewer.getCombo();
				prevButton.setEnabled(combo.getSelectionIndex() > 0);
				nextButton.setEnabled(combo.getSelectionIndex() >= 0 &&
						combo.getSelectionIndex() < combo.getItemCount() - 1);
			}
			
		});
		
		updateNow();
		
		// update after selection change
		selectionService.addSelectionListener(this);
		
		// update after alignment change
		alignmentService.addListener(alignmentListener = new HaleServiceListener() {

			@Override
			public void update(@SuppressWarnings("rawtypes") UpdateMessage message) {
				if (Display.getCurrent() != null) {
					CellSelector.this.update(
							CellSelector.this.lastSelection);
				}
				else {
					final Display display = PlatformUI.getWorkbench().getDisplay();
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							CellSelector.this.update(
									CellSelector.this.lastSelection);
						}
					});
				}
			}
			
		});
		
		// update default schema selection
		SchemaItemService itemService = (SchemaItemService) PlatformUI.getWorkbench().getService(SchemaItemService.class);
		itemService.addListener(itemListener = new SchemaServiceAdapter() {

			/**
			 * @see SchemaServiceAdapter#schemaChanged(SchemaType)
			 */
			@Override
			public void schemaChanged(SchemaType schema) {
				if (!useViewSelection) {
					if (Display.getCurrent() != null) {
						updateNow();
					}
					else {
						final Display display = PlatformUI.getWorkbench().getDisplay();
						display.syncExec(new Runnable() {
							
							@Override
							public void run() {
								updateNow();
							}
							
						});
					}
				}
			}
		});
	}

	private void updateNow() {
		// update now
		if (useViewSelection) {
			update((lastViewSelection == null)?(selectionService.getSelection()):(lastViewSelection));
		}
		else {
			update(getDefaultSchemaSelection());
		}
	}

	/**
	 * Get the default schema selection
	 * 
	 * @return the default schema selection
	 */
	private SchemaSelection getDefaultSchemaSelection() {
		SchemaItemService schemaItemService = (SchemaItemService) PlatformUI.getWorkbench().getService(SchemaItemService.class);
		return new SchemaSelection(
				Collections.singleton(schemaItemService.getRoot(SchemaType.SOURCE)),
				Collections.singleton(schemaItemService.getRoot(SchemaType.TARGET)));
	}

	/**
	 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof SchemaSelection)) {
			return;
		}
		
		lastViewSelection = selection;
		
		if (useViewSelection) {
			update(selection);
		}
	}
	
	/**
	 * Update with the given selection
	 * 
	 * @param selection the selection
	 */
	private void update(ISelection selection) {
		lastSelection = selection;
		
		Map<ICell, CellInfo> cells = null;
		
		if (selection != null && !selection.isEmpty()
				&& selection instanceof SchemaSelection) {
			SchemaSelection schema = (SchemaSelection) selection;
			cells = schema.getCellsForSelection();
		}
		
		List<CellInfo> cellList = (cells == null)?(new ArrayList<CellInfo>()):(new ArrayList<CellInfo>(cells.values()));
		
		// set the input
		viewer.setInput(cellList);
		
		// update the selection & state
		if (cellList.isEmpty()) {
			viewer.setSelection(StructuredSelection.EMPTY);
			viewer.getControl().setEnabled(false);
			lastSelected = null; // set last selected to null because it is invalid now
		}
		else {
			if (lastSelected != null && cellList.contains(lastSelected)) {
				viewer.setSelection(new StructuredSelection(lastSelected));
			} else {
				viewer.setSelection(new StructuredSelection(cellList.get(0)));
			}
			viewer.getControl().setEnabled(true);
		}
	}

	/**
	 * Recursively get the children of the given items
	 * 
	 * @param items the items
	 * @return the set of children
	 */
	private Set<? extends SchemaItem> getChildren(
			Set<SchemaItem> items) {
		Set<SchemaItem> children = new LinkedHashSet<SchemaItem>();
		
		// add children
		for (SchemaItem item : items) {
			if (item.hasChildren()) {
				for (SchemaItem child : item.getChildren()) {
					children.add(child);
				}
			}
		}
		
		if (!children.isEmpty()) {
			children.addAll(getChildren(children));
		}
		
		return children;
	}

	/**
	 * Get the control
	 *  
	 * @return the control
	 */
	public Control getControl() {
		return page;
	}
	
	/**
	 * Update the selection and fire a selection change
	 * 
	 * @param cell the new selection
	 */
	protected void fireCellSelectionChange(CellInfo cell) {
		selected = cell;
		
		if (cell != null) {
			cellSelection = new CellSelection(cell);
		} else {
			cellSelection = new CellSelection();
		}
		
		SelectionChangedEvent event = new SelectionChangedEvent(this, cellSelection);
		
		for (ISelectionChangedListener listener : listeners) {
			try {
				listener.selectionChanged(event);
			} catch (Exception e) {
				log.error("Error while notifying listener", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @see IDisposable#dispose()
	 */
	@Override
	public void dispose() {
		alignmentService.removeListener(alignmentListener);
		
		selectionService.removeSelectionListener(this);
		
		SchemaItemService itemService = (SchemaItemService) PlatformUI.getWorkbench().getService(SchemaItemService.class);
		itemService.removeListener(itemListener);
		
		prevImage.dispose();
		nextImage.dispose();
		editImage.dispose();
		deleteImage.dispose();
		synchImage.dispose();
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
		
		if (cellSelection != null) {
			listener.selectionChanged(new SelectionChangedEvent(this, cellSelection));
		}
	}

	/**
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return cellSelection;
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		this.cellSelection = selection;
	}

}
