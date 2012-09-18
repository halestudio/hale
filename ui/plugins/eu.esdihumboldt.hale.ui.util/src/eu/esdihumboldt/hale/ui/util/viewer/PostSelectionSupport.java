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

package eu.esdihumboldt.hale.ui.util.viewer;

import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * Selection provider that adds support for post selection events to a wrapped
 * selection provider.
 * 
 * @author Simon Templer
 */
public class PostSelectionSupport implements IPostSelectionProvider, ISelectionProvider {

	private final ISelectionProvider selectionProvider;

	/**
	 * Create post selection support for the given selection provider
	 * 
	 * @param selectionProvider the selection provider
	 */
	public PostSelectionSupport(ISelectionProvider selectionProvider) {
		super();
		this.selectionProvider = selectionProvider;
	}

	/**
	 * Delegates to
	 * {@link #addSelectionChangedListener(ISelectionChangedListener)}
	 * 
	 * @see IPostSelectionProvider#addPostSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
		addSelectionChangedListener(listener);
	}

	/**
	 * Delegates to
	 * {@link #removeSelectionChangedListener(ISelectionChangedListener)}
	 * 
	 * @see IPostSelectionProvider#removePostSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
		removeSelectionChangedListener(listener);
	}

	/**
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProvider.addSelectionChangedListener(listener);
	}

	/**
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return selectionProvider.getSelection();
	}

	/**
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionProvider.removeSelectionChangedListener(listener);
	}

	/**
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		selectionProvider.setSelection(selection);
	}

}
