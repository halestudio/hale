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

package eu.esdihumboldt.hale.ui.util.viewer.tree;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

/**
 * Adapter for using an {@link ITreeContentProvider} as an
 * {@link ITreePathContentProvider}. Does not support providing parent tree
 * paths. It however also implements {@link ITreeContentProvider} and calls the
 * adaptees {@link #getParent(Object)} method.
 * 
 * @see #getParents(Object)
 * 
 * @author Simon Templer
 */
public class TreePathProviderAdapter implements ITreePathContentProvider, ITreeContentProvider {

	private static final TreePath[] EMPTY_PATHS = new TreePath[] {};

	private final ITreeContentProvider contentProvider;

	/**
	 * Create an {@link ITreePathContentProvider} wrapping an
	 * {@link ITreeContentProvider}.
	 * 
	 * @param contentProvider the tree content provider to wrap
	 */
	public TreePathProviderAdapter(ITreeContentProvider contentProvider) {
		super();
		this.contentProvider = contentProvider;
	}

	/**
	 * @see IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		contentProvider.dispose();
	}

	/**
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		contentProvider.inputChanged(viewer, oldInput, newInput);
	}

	/**
	 * @see ITreePathContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return contentProvider.getElements(inputElement);
	}

	/**
	 * @see ITreePathContentProvider#getChildren(TreePath)
	 */
	@Override
	public Object[] getChildren(TreePath parentPath) {
		return contentProvider.getChildren(parentPath.getLastSegment());
	}

	/**
	 * @see ITreePathContentProvider#hasChildren(TreePath)
	 */
	@Override
	public boolean hasChildren(TreePath path) {
		return contentProvider.hasChildren(path.getLastSegment());
	}

	/**
	 * Will return an empty array.
	 * 
	 * @see ITreePathContentProvider#getParents(Object)
	 */
	@Override
	public TreePath[] getParents(Object element) {
		return EMPTY_PATHS;
	}

	/**
	 * Get the internal tree content provider.
	 * 
	 * @return the internal tree content provider
	 */
	public ITreeContentProvider getTreeContentProvider() {
		return contentProvider;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		return contentProvider.getChildren(parentElement);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		return contentProvider.getParent(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		return contentProvider.hasChildren(element);
	}

}
