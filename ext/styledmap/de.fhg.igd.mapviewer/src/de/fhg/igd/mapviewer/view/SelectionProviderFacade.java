/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.view;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Based on AbstractSelectionMediator
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public class SelectionProviderFacade implements ISelectionProvider {

	/**
	 * The object whose events shall be filtered
	 */
	private ISelectionProvider _decoratee;

	/**
	 * A list of selection listeners
	 */
	private final ListenerList<ISelectionChangedListener> _selectionListeners = new ListenerList<>();

	private final ISelectionChangedListener selectionListener = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			doSelectionChanged();
		}
	};

	/**
	 * Set the current selection provider
	 * 
	 * @param prov the selection provider
	 */
	public void setSelectionProvider(ISelectionProvider prov) {
		if (_decoratee != null) {
			_decoratee.removeSelectionChangedListener(selectionListener);
		}

		_decoratee = prov;

		if (_decoratee != null) {
			_decoratee.addSelectionChangedListener(selectionListener);
		}
	}

	/**
	 * @return the object whose events shall be filtered
	 */
	public ISelectionProvider getDecoratee() {
		return _decoratee;
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		_selectionListeners.add(listener);
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		_selectionListeners.remove(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (_decoratee != null) {
			_decoratee.setSelection(selection);
		}
		fireSelectionChanged();
	}

	/**
	 * This method will be called when the selection of the decoratee changed
	 */
	private void doSelectionChanged() {
		fireSelectionChanged();
	}

	/**
	 * Notifies the selection listeners about a new selection
	 */
	private void fireSelectionChanged() {
		if (Display.getCurrent() != null) {
			fireSelectionChangedInternal();
		}
		else {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					fireSelectionChangedInternal();
				}
			});
		}
	}

	/**
	 * Notifies the selection listeners about a new selection, should be called
	 * only by {@link #fireSelectionChanged()}
	 */
	private void fireSelectionChangedInternal() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

		Object[] listeners = _selectionListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
			listener.selectionChanged(event);
		}
	}

	/**
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		if (_decoratee != null) {
			return _decoratee.getSelection();
		}
		else {
			return null;
		}
	}
}
