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
package eu.esdihumboldt.hale.rcp.views.mapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IDisposable;

import eu.esdihumboldt.cst.align.ICell;
import eu.esdihumboldt.cst.align.IEntity;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.model.SchemaItem;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * Control for selecting a cell, reacts on {@link SchemaSelection}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class CellSelector implements ISelectionListener, IDisposable, ISelectionProvider {
	
	private static final Log log = LogFactory.getLog(CellSelector.class);
	
	private final ComboViewer viewer;
	
	private final AlignmentService alignmentService;
	
	/**
	 * The last schema selection
	 */
	private ISelection lastSelection = null;
	
	private ICell lastSelected = null;
	
	/**
	 * The currently selected cell
	 */
	private ICell selected = null;
	
	/**
	 * The current cell selection
	 */
	private ISelection cellSelection;
	
	private final Composite page;
	
	private final Set<ISelectionChangedListener> listeners = new HashSet<ISelectionChangedListener>();
	
	private final Image prevImage;
	
	private final Image nextImage;
	
	private final Image editImage;
	
	private final Image deleteImage;
	
	private final Button prevButton;
	
	private final Button nextButton;
	
	/**
	 * Constructor
	 * 
	 * @param parent the parent composite
	 * @param selectionService the selection service
	 */
	public CellSelector(Composite parent, ISelectionService selectionService) {
		super();
		
		alignmentService = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		
		// composite
		page = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(5, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 2;
		page.setLayout(layout);
		
		// navigation buttons
		prevButton = new Button(page, SWT.PUSH);
		prevButton.setEnabled(false);
		prevImage = HALEActivator.getImageDescriptor("icons/backward_nav.gif").createImage();
		prevButton.setImage(prevImage);
		prevButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see SelectionAdapter#widgetSelected(SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object element = viewer.getElementAt(viewer.getCombo().getSelectionIndex() - 1);
				viewer.setSelection(new StructuredSelection(element));
			}
			
		});
		
		nextButton = new Button(page, SWT.PUSH);
		nextButton.setEnabled(false);
		nextImage = HALEActivator.getImageDescriptor("icons/forward_nav.gif").createImage();
		nextButton.setImage(nextImage);
		nextButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see SelectionAdapter#widgetSelected(SelectionEvent)
			 */
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
		viewer.setLabelProvider(new LabelProvider() {

			/**
			 * @see LabelProvider#getText(Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof ICell) {
					ICell cell = (ICell) element;
					return getShortName(cell.getEntity1()) +
						" - " + getShortName(cell.getEntity2());
				}
				else {
					return super.getText(element);
				}
			}
			
		});
		
		// edit buttons
		final Button editButton = new Button(page, SWT.PUSH);
		editImage = HALEActivator.getImageDescriptor("icons/editor_area.gif").createImage();
		editButton.setImage(editImage);
		//TODO
		
		final Button deleteButton = new Button(page, SWT.PUSH);
		deleteImage = HALEActivator.getImageDescriptor("icons/delete_edit.gif").createImage();
		deleteButton.setImage(deleteImage);
		deleteButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see SelectionAdapter#widgetSelected(SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selected != null) {
					alignmentService.removeCell(selected);
				}
			}
			
		});
		
		
		// edit buttons state
		addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				boolean enabled = !event.getSelection().isEmpty();
				editButton.setEnabled(false); //XXX editButton.setEnabled(enabled);
				deleteButton.setEnabled(enabled);
			}
			
		});
		
		// initial selection event
		fireCellSelectionChange(null);
		
		// update now
		update(selectionService.getSelection());
		
		// update after selection change
		selectionService.addSelectionListener(this);
		
		// update after alignment change
		alignmentService.addListener(new HaleServiceListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void update(UpdateMessage message) {
				CellSelector.this.update(
						CellSelector.this.lastSelection);
			}
			
		});
		
		// react on cell selection changes
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				
				if (selection == null || selection.isEmpty()) {
					fireCellSelectionChange(null);
				}
				else {
					ICell selectedCell = (ICell) ((StructuredSelection) selection).getFirstElement();
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
	}

	/**
	 * @see ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof SchemaSelection)) {
			return;
		}
		
		update(selection);
	}
	
	/**
	 * Update with the given selection
	 * 
	 * @param selection the selection
	 */
	private void update(ISelection selection) {
		lastSelection = selection;
		
		List<ICell> cells = new ArrayList<ICell>();
		
		if (selection != null && !selection.isEmpty()
				&& selection instanceof SchemaSelection) {
			SchemaSelection schema = (SchemaSelection) selection;
			
			Set<SchemaItem> sourceItems = new LinkedHashSet<SchemaItem>(schema.getSourceItems());
			Set<SchemaItem> targetItems = new LinkedHashSet<SchemaItem>(schema.getTargetItems());
			
			sourceItems.addAll(getChildren(sourceItems));
			targetItems.addAll(getChildren(targetItems));
			
			if (sourceItems != null && targetItems != null) {
				// for each source item...
				for (SchemaItem source : sourceItems) {
					// ...for each target item...
					for (SchemaItem target : targetItems) {
						// ...find the mapping cells
						ICell cell = alignmentService.getCell(
								source.getEntity(),
								target.getEntity());
						
						if (cell != null) {
							cells.add(cell);
						}
					}
				}
			}
		}
		
		// set the input
		viewer.setInput(cells);
		
		// update the selection & state
		if (cells.isEmpty()) {
			viewer.setSelection(StructuredSelection.EMPTY);
			viewer.getControl().setEnabled(false);
		}
		else {
			if (lastSelected != null && cells.contains(lastSelected)) {
				viewer.setSelection(new StructuredSelection(lastSelected));
			} else {
				viewer.setSelection(new StructuredSelection(cells.get(0)));
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
	
	protected void fireCellSelectionChange(ICell cell) {
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
				log.error("Error while notifying listener", e);
			}
		}
	}
	
	/**
	 * Get a short name for the given entity
	 * 
	 * @param entity the entity
	 * @return the short name
	 */
	public static String getShortName(IEntity entity) {
		List<String> label = entity.getLabel();
		if (label.size() == 2) {
			return label.get(1);
		}
		else if (label.size() > 2) {
			return label.get(label.size() - 2) + "." + label.get(label.size() - 1);
		}
		else if (label.size() == 1) {
			return label.get(0);
		}
		
		return "unnamed";
	}

	/**
	 * @see IDisposable#dispose()
	 */
	@Override
	public void dispose() {
		prevImage.dispose();
		nextImage.dispose();
		editImage.dispose();
		deleteImage.dispose();
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
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
