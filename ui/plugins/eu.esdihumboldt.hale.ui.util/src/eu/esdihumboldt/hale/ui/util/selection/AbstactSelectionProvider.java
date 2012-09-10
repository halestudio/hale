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

import de.cs3d.util.eclipse.TypeSafeListenerList;

/**
 * Abstract selection provider implementation
 * 
 * @author Simon Templer
 */
public class AbstactSelectionProvider implements ISelectionProvider {

	private ISelection lastSelection;

	private final TypeSafeListenerList<ISelectionChangedListener> listeners = new TypeSafeListenerList<ISelectionChangedListener>();

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
		return lastSelection;
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		this.lastSelection = selection;
	}

	/**
	 * Fires a selection change and sets the last selection to the given
	 * selection.
	 * 
	 * @param newSelection the new selection
	 */
	protected void fireSelectionChange(ISelection newSelection) {
		this.lastSelection = newSelection;

		SelectionChangedEvent event = new SelectionChangedEvent(this, newSelection);

		for (ISelectionChangedListener listener : listeners) {
			listener.selectionChanged(event);
		}
	}

	/**
	 * Get the last selection
	 * 
	 * @return the last selection
	 */
	protected ISelection getLastSelection() {
		return lastSelection;
	}

}
