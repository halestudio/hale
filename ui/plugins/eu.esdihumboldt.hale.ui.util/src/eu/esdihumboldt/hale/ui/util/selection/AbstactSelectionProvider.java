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

package eu.esdihumboldt.hale.ui.util.selection;

import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Abstract selection provider implementation
 * 
 * @author Simon Templer
 */
public class AbstactSelectionProvider implements ISelectionProvider {

	private ISelection lastSelection;

	private final CopyOnWriteArraySet<ISelectionChangedListener> listeners = new CopyOnWriteArraySet<ISelectionChangedListener>();

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
