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

package eu.esdihumboldt.hale.ui.util.viewer.tree;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

/**
 * Adapter for using an {@link ITreeContentProvider} as an
 * {@link ITreePathContentProvider}. Does not support providing parent tree
 * paths.
 * 
 * @see #getParents(Object)
 * 
 * @author Simon Templer
 */
public class TreePathProviderAdapter implements ITreePathContentProvider {

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

}
