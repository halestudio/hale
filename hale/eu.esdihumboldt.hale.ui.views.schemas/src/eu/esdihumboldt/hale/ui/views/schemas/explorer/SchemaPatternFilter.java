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

package eu.esdihumboldt.hale.ui.views.schemas.explorer;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TreePath;

import eu.esdihumboldt.hale.ui.views.schemas.explorer.tree.TreePathPatternFilter;

/**
 * Pattern filter that doesn't descend below a maximum level and doesn't follow
 * cycles.
 * @author Simon Templer
 */
public class SchemaPatternFilter extends TreePathPatternFilter {

	private static final int MAX_LEVELS = 8;

	/**
	 * @see TreePathPatternFilter#allowDescend(TreePath)
	 */
	@Override
	protected boolean allowDescend(TreePath elementPath) {
		if (elementPath != null) {
			// don't descend below a max depth
			//TODO configurable
			if (elementPath.getSegmentCount() > MAX_LEVELS) {
				return false;
			}
			
			Set<Object> segments = new HashSet<Object>();
			for (int i = 0; i < elementPath.getSegmentCount(); i++) {
				Object segment = elementPath.getSegment(i);
				if (segments.contains(segment)) {
					// break if there is a cycle
					return false;
				}
				segments.add(segment);
			}
		}
		
		return true;
	}

}
