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

	private final Set<Object> memoryAccepted = new HashSet<Object>();
	private final Set<Object> memoryRejected = new HashSet<Object>();

	/**
	 * @see eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter#setPattern(java.lang.String)
	 */
	@Override
	public void setPattern(String patternString) {
		super.setPattern(patternString);
		memoryAccepted.clear();
		memoryRejected.clear();
	}

	/**
	 * @see TreePathPatternFilter#allowDescend(TreePath)
	 */
	@Override
	protected boolean allowDescend(TreePath elementPath) {
		if (elementPath != null) {
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
		return memoryAccepted.contains(segment);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.viewer.tree.TreePathPatternFilter#isElementVisible(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isElementVisible(Viewer viewer, Object element) {
		TreePath elementPath = (TreePath) element;
		Object segment = elementPath.getLastSegment();
		if (segment instanceof EntityDefinition) {
			segment = ((EntityDefinition) segment).getDefinition();
		}

		if (memoryAccepted.contains(segment))
			return true;
		if (memoryRejected.contains(segment))
			return false;

		boolean match = isLeafMatch(viewer, element);

		if (!match) {
			match = isParentMatch(viewer, element);

			if (!match) {
				memoryRejected.add(segment);
			}
		}
		if (match) {
			memoryAccepted.add(segment);
		}
		return match;
	}
}
