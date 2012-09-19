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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Filtered tree that supports a {@link ITreePathContentProvider} and a
 * {@link TreePathPatternFilter}
 * 
 * @author Simon Templer
 */
public class TreePathFilteredTree extends FilteredTree {

	/**
	 * Tree viewer that calls filters with {@link TreePath}s as elements
	 */
	public static class TreePathTreeViewer extends /* Notifying */TreeViewer {

		/**
		 * @see TreeViewer#TreeViewer(Composite, int)
		 */
		public TreePathTreeViewer(Composite parent, int style) {
			super(parent, style);
		}

		/**
		 * @see StructuredViewer#getFilteredChildren(Object)
		 */
		@Override
		protected Object[] getFilteredChildren(Object parent) {
			Object[] elements = getRawChildren(parent);

			if (parent != null && !(parent instanceof TreePath)) {
				// root element
				parent = TreePath.EMPTY;
			}

			TreePath parentPath = (TreePath) parent;

			// create tree paths from elements
			List<TreePath> paths = getPathsForElements(parentPath, elements);

			List<ViewerFilter> filters = Arrays.asList(getFilters());
			if (filters != null) {
				for (Iterator<ViewerFilter> iter = filters.iterator(); iter.hasNext();) {
					ViewerFilter f = iter.next();
					// call filter with TreePath objects
					Object[] filteredResult = f.filter(this, parent, paths.toArray());
					paths.clear();
					for (Object res : filteredResult) {
						paths.add((TreePath) res);
					}
				}
			}

			List<Object> result = new ArrayList<Object>();
			for (TreePath path : paths) {
				result.add(path.getLastSegment());
			}

			return result.toArray();
		}

	}

	/**
	 * @see FilteredTree#FilteredTree(Composite, int, PatternFilter, boolean)
	 */
	public TreePathFilteredTree(Composite parent, int treeStyle, PatternFilter filter,
			boolean useNewLook) {
		super(parent, treeStyle, filter, useNewLook);
	}

	/**
	 * @see FilteredTree#doCreateTreeViewer(Composite, int)
	 */
	@Override
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		return new TreePathTreeViewer(parent, style);
	}

	/**
	 * Get the tree paths for the given objects with the given parent path.
	 * 
	 * @param parentPath the parent path for each object
	 * @param elements the objects
	 * @return the list of tree paths
	 */
	public static List<TreePath> getPathsForElements(TreePath parentPath, Object[] elements) {
		List<TreePath> paths = new ArrayList<TreePath>();
		// create tree paths from elements
		List<Object> parentSegments = new ArrayList<Object>();
		if (parentPath != null) {
			for (int i = 0; i < parentPath.getSegmentCount(); i++) {
				parentSegments.add(parentPath.getSegment(i));
			}
		}
		for (Object element : elements) {
			TreePath path;
			if (parentPath == null) {
				path = new TreePath(new Object[] { element });
			}
			else {
				parentSegments.add(element);
				path = new TreePath(parentSegments.toArray());
				parentSegments.remove(parentSegments.size() - 1);
			}
			paths.add(path);
		}
		return paths;
	}

}
