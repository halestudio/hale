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

package eu.esdihumboldt.hale.ui.util.selection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.services.IDisposable;

/**
 * Selection provider that wraps another selection provider and filters
 * selections before they are passed to an external object.
 * 
 * @author Simon Templer
 */
public abstract class SelectionFilter extends AbstactSelectionProvider implements IDisposable {

	private final ISelectionProvider selectionProvider;
	private ISelectionChangedListener listener;

	/**
	 * Create a selection filter
	 * 
	 * @param selectionProvider the internal selection provider
	 */
	public SelectionFilter(ISelectionProvider selectionProvider) {
		super();
		this.selectionProvider = selectionProvider;

		selectionProvider.addSelectionChangedListener(listener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fireSelectionChange(filter(event.getSelection()));
			}
		});
	}

	/**
	 * Filter a selection from the internal selection provider that will be
	 * passed outside
	 * 
	 * @param selection the internal selection
	 * @return the selection to used externally
	 */
	protected abstract ISelection filter(ISelection selection);

	/**
	 * @see AbstactSelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return filter(super.getSelection());
	}

	/**
	 * @see IDisposable#dispose()
	 */
	@Override
	public void dispose() {
		if (listener != null) {
			selectionProvider.removeSelectionChangedListener(listener);
		}
	}

}
