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

package eu.esdihumboldt.hale.common.align.model.transformation.tree.context;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;

/**
 * Identifies context matches between source and target nodes in a
 * transformation tree.
 * 
 * @author Simon Templer
 */
public interface ContextMatcher {

	/**
	 * Find the context matches in the given transformation tree and applies
	 * {@link TransformationContext}s to the {@link SourceNode}s in the tree (if
	 * possible)
	 * 
	 * @param tree the transformation tree
	 */
	public void findMatches(TransformationTree tree);

}
