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

package eu.esdihumboldt.hale.ui.common.definition.viewer;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter;

/**
 * Pattern filter that doesn't descend below a maximum level and doesn't follow
 * cycles.
 * 
 * @author Simon Templer
 */
public class SchemaPatternFilter extends TreePathPatternFilter {

	private static final int MAX_LEVELS = 8;

	private Set<Object> memory = new HashSet<Object>();

	/**
	 * @see eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter#setPattern(java.lang.String)
	 */
	@Override
	public void setPattern(String patternString) {
		super.setPattern(patternString);
		memory.clear();
	}

	/**
	 * @see TreePathPatternFilter#allowDescend(TreePath)
	 */
	@Override
	protected boolean allowDescend(TreePath elementPath) {
		if (elementPath != null) {
			// don't descend below a max depth
			/*
			 * XXX Reintroduced this as we get into trouble with large schemas,
			 * searching the whole tree (even though cycles are skipped) just
			 * takes to long. FIXME any ideas on a better solution?
			 */
			if (elementPath.getSegmentCount() > MAX_LEVELS) {
				return false;
			}

			Set<Object> segments = new HashSet<Object>();
			for (int i = 0; i < elementPath.getSegmentCount(); i++) {
				Object segment = elementPath.getSegment(i);
				if (segment instanceof EntityDefinition) {
					segment = ((EntityDefinition) segment).getDefinition();
				}
				if (segments.contains(segment)) {
					// break if there is a cycle
					return false;
				}
				segments.add(segment);
			}
		}

		return true;
	}

	/**
	 * Check if the current (leaf) element is a match with the chosen criteria. <br>
	 * The default behavior checks that the label of the element is a match with
	 * the filter text. Subclasses should override this method.<br>
	 * <br>
	 * Subclasses which add additional criteria should disable early termination
	 * ({@link TreePathPatternFilter#setUseEarlyReturnIfMatcherIsNull(boolean)}
	 * ).
	 * 
	 * @param viewer the viewer that contains the element
	 * @param element the tree element to check
	 * @return true if the given element's label matches the chosen criteria
	 */
	protected boolean matches(Viewer viewer, Object element) {
		return super.isLeafMatch(viewer, element);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter#isLeafMatch(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object)
	 */
	@Override
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		boolean leaf = matches(viewer, element);
		if (leaf) {
			return true;
		}

		// return true, if this element' definition was classified as visible
		TreePath elementPath = (TreePath) element;
		Object segment = elementPath.getLastSegment();
		if (segment instanceof EntityDefinition) {
			segment = ((EntityDefinition) segment).getDefinition();
		}
		return memory.contains(segment);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter#isElementVisible(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isElementVisible(Viewer viewer, Object element) {
		boolean match = isLeafMatch(viewer, element) || isParentMatch(viewer, element);
		if (match) {
			TreePath elementPath = (TreePath) element;
			Object segment = elementPath.getLastSegment();
			if (segment instanceof EntityDefinition) {
				segment = ((EntityDefinition) segment).getDefinition();
			}
			memory.add(segment);
		}
		return match;
	}
}
