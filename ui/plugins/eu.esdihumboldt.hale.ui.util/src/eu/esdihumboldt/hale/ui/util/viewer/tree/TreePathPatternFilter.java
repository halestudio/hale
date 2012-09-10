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

import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Adapts {@link PatternFilter} to support {@link TreePath}s as elements. Using
 * a cache is not supported.
 */
public class TreePathPatternFilter extends PatternFilter {

	private boolean hasMatcher = false;
	private boolean useEarlyReturnIfMatcherIsNull = true;

	/**
	 * Similar to {@link PatternFilter} but without caching. Needed by
	 * {@link #isParentMatch(Viewer, Object)}
	 * 
	 * @see PatternFilter#isAnyVisible(Viewer, Object, Object[])
	 */
	@SuppressWarnings("javadoc")
	private boolean isAnyVisible(Viewer viewer, @SuppressWarnings("unused") Object parent,
			Object[] elements) {
		if (!hasMatcher && useEarlyReturnIfMatcherIsNull) {
			return true;
		}

		return computeAnyVisible(viewer, elements);
	}

	/**
	 * Similar to the implementation in {@link PatternFilter}. Needed by
	 * {@link #isAnyVisible(Viewer, Object, Object[])}
	 * 
	 * @see PatternFilter#computeAnyVisible(Viewer, Object[])
	 */
	@SuppressWarnings("javadoc")
	private boolean computeAnyVisible(Viewer viewer, Object[] elements) {
		boolean elementFound = false;
		for (int i = 0; i < elements.length && !elementFound; i++) {
			Object element = elements[i];
			elementFound = isElementVisible(viewer, element);
		}
		return elementFound;
	}

	/**
	 * @see PatternFilter#setPattern(String)
	 */
	@Override
	public void setPattern(String patternString) {
		if (patternString == null || patternString.equals("")) { //$NON-NLS-1$
			hasMatcher = false;
		}
		else {
			hasMatcher = true;
		}

		super.setPattern(patternString);
	}

	/**
	 * Sets whether checks should return true directly if the matcher text is
	 * empty.
	 * 
	 * @param value the value
	 */
	public void setUseEarlyReturnIfMatcherIsNull(boolean value) {
		useEarlyReturnIfMatcherIsNull = value;
		if (value)
			setPattern("org.eclipse.ui.keys.optimization.true");
		else
			setPattern("org.eclipse.ui.keys.optimization.false");
	}

	/**
	 * @see PatternFilter#isParentMatch(Viewer, Object)
	 */
	@Override
	protected boolean isParentMatch(Viewer viewer, Object element) {
		TreePath elementPath = (TreePath) element;

		boolean proceed = allowDescend(elementPath);
		if (!proceed) {
			return false;
		}

		Object[] children;
		ITreePathContentProvider cp = ((ITreePathContentProvider) ((AbstractTreeViewer) viewer)
				.getContentProvider());

		children = cp.getChildren(elementPath);

		if ((children != null) && (children.length > 0)) {
			// convert children to tree paths
			List<TreePath> pathChildren = TreePathFilteredTree.getPathsForElements(elementPath,
					children);

			return isAnyVisible(viewer, element, pathChildren.toArray());
		}
		return false;
	}

	/**
	 * Determines if descending further is allowed. The default implementation
	 * returns <code>true</code>.
	 * 
	 * @param elementPath the current element path, may be <code>null</code>
	 * @return if descending further is allowed
	 */
	protected boolean allowDescend(TreePath elementPath) {
		return true;
	}

	/**
	 * @see PatternFilter#isLeafMatch(Viewer, Object)
	 */
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		if (element instanceof TreePath) { // support TreePath
			element = ((TreePath) element).getLastSegment();
		}

		String labelText = ((ILabelProvider) ((StructuredViewer) viewer).getLabelProvider())
				.getText(element);

		if (labelText == null) {
			return false;
		}
		return wordMatches(labelText);
	}

}
