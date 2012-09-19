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
