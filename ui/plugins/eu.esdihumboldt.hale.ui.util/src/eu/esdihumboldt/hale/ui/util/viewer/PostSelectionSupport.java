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
