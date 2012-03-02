/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.util.selector;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.cs3d.util.eclipse.TypeSafeListenerList;
import eu.esdihumboldt.hale.ui.util.viewer.ObjectContentProvider;

/**
 * Abstract selector control based on a {@link TableViewer}.
 * @param <T> the type of the object to be selected
 * @author Simon Templer
 */
public abstract class AbstractSelector<T> implements ISelectionProvider {
	
	/**
	 * Wraps a label provider and provides additionally a text for
	 * {@link NoObject#NONE}.
	 */
	public class SelectorLabelProvider implements ILabelProvider {

		private final ILabelProvider labelProvider;

		/**
		 * Create a selector label provider.
		 * @param labelProvider the label provider that it is based on
		 */
		public SelectorLabelProvider(ILabelProvider labelProvider) {
			this.labelProvider = labelProvider;
		}

		@Override
		public Image getImage(Object element) {
			if (element == NoObject.NONE) {
				return null;
			}
			return labelProvider.getImage(element);
		}

		@Override
		public String getText(Object element) {
			if (element == NoObject.NONE) {
				return "<Click to select>";
			}
			return labelProvider.getText(element);
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			labelProvider.addListener(listener);
		}

		@Override
		public void dispose() {
			labelProvider.dispose();
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return labelProvider.isLabelProperty(element, property);
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			labelProvider.removeListener(listener);
		}

	}

	private static enum NoObject {
		NONE
	}
	
	private final TypeSafeListenerList<ISelectionChangedListener> listeners = new TypeSafeListenerList<ISelectionChangedListener>();
	
	private final TableViewer viewer;
	
	private final Composite main;
	
//	private final F field;
	
	private final ViewerFilter[] filters;
	
	/**
	 * Tracks the current input. Set by inputChanged of the content provider.
	 */
	private Object currentInput;

	/**
	 * Create a selector.
	 * @param parent the parent composite
	 * @param labelProvider the label provider for the selector
	 * @param filters the filters for the selector, may be <code>null</code>
	 */
	public AbstractSelector(Composite parent, ILabelProvider labelProvider,
			ViewerFilter[] filters) {
//		this.field = field;
		
		main = new Composite(parent, SWT.NONE);
		TableColumnLayout columnLayout = new TableColumnLayout();
		main.setLayout(columnLayout);
		
		// entity selection combo
		viewer = new TableViewer(main, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.NO_SCROLL);
		
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		columnLayout.setColumnData(column.getColumn(), new ColumnWeightData(1, false));

		viewer.setContentProvider(ObjectContentProvider.getInstance());
		viewer.setLabelProvider(new SelectorLabelProvider(labelProvider));
//		viewer.setLabelProvider(new DefinitionLabelProvider(true, true) {
//			@Override
//			public String getText(Object element) {
//				if (element == NoObject.NONE) {
//					return "<Click to select>";
//				}
//				return super.getText(element);
//			}
//		});

		this.filters = filters;
		
		// initial selection
		Object select = NoObject.NONE;
		currentInput = select;
		viewer.setInput(select);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					return;
				}
				
				AbstractTreeSelectionDialog<T> dialog = createSelectionDialog(
						Display.getCurrent().getActiveShell());
				dialog.setFilters(AbstractSelector.this.filters);
				if (dialog.open() == AbstractTreeSelectionDialog.OK) {
					T selected = dialog.getObject();
					if ((selected == null && currentInput == null) || (selected != null && selected.equals(currentInput)))
						return;
					if (selected != null) {
						currentInput = selected;
					}
					else {
						currentInput = NoObject.NONE;
					}
					viewer.setInput(currentInput);
					/*
					 * XXX Bug on Mac? - Viewer is not refreshed correctly until 
					 * user clicks on the wizard.
					 * Manually refreshing, layouting the parent composite or
					 * calling forceActive/forceFocus/setActive on the Shell 
					 * doesn't help.
					 * XXX is this fixed with TableColumnLayout?
					 */
				}
				viewer.setSelection(new StructuredSelection());

				// inform about the input change
				fireSelectionChange();
			}
			
		});
	}
	
	/**
	 * Determines if the given object matches the selector's filters.
	 *
	 * @param candidate the object to test
	 * @return if the object is accepted by all filters
	 */
	public boolean accepts(Object candidate) {
		return AbstractTreeSelectionDialog.acceptObject(viewer, filters, candidate);
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
		Object input = currentInput;
		if (input == null || input == NoObject.NONE) {
			return new StructuredSelection();
		}
		return new StructuredSelection(input);
	}

//	/**
//	 * Get the field definition associated with the selector.
//	 * @return the field definition
//	 */
//	public F getField() {
//		return field;
//	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();
			if (selected != null) {
				// run against filters
				if (AbstractTreeSelectionDialog.acceptObject(viewer, filters, selected)) {
					// valid selection
					currentInput = selected;
					viewer.setInput(selected);
					
					fireSelectionChange();
					
					return;
				}
				else {
					//TODO user error message?
				}
			}
		}
		
		currentInput = NoObject.NONE;
		viewer.setInput(NoObject.NONE);
		viewer.setSelection(new StructuredSelection());
		
		fireSelectionChange();
	}
	
	/**
	 * Fires a selection change and sets the last selection to the given 
	 * selection.
	 */
	protected void fireSelectionChange() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		
		for (ISelectionChangedListener listener : listeners) {
			listener.selectionChanged(event);
		}
	}

	/**
	 * Create the dialog for selecting an entity.
	 * @param parentShell the parent shell for the dialog
	 * @return the entity dialog
	 */
	protected abstract AbstractTreeSelectionDialog<T> createSelectionDialog(Shell parentShell);

	/**
	 * Get the main selector control
	 * @return the main control
	 */
	public Control getControl() {
		return main;
	}

	/**
	 * Get the selected entity definition
	 * @return the selected entity definition or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public T getSelectedObject() {
		ISelection selection = getSelection();
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return null;
		}
		
		Object element = ((IStructuredSelection) selection).getFirstElement();
		if (element != null && element != NoObject.NONE) {
			return (T) element;
		}
		
		return null;
	}
	
//	/**
//	 * Get the selected entity
//	 * @return the selected entity or <code>null</code>
//	 */
//	public Entity getEntity() {
//		EntityDefinition def = getSelectedObject();
//		
//		if (def != null) {
//			return createEntity(def);
//		}
//		
//		return null;
//	}
//
//	/**
//	 * Create an entity for the given entity definition
//	 * @param element the entity definition
//	 * @return the entity
//	 */
//	protected abstract Entity createEntity(EntityDefinition element);

}
