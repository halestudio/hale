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

import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;

/**
 * Duplicates a context sub-tree in a transformation tree.
 * 
 * @author Simon Templer
 */
public interface TransformationContext {

	/**
	 * Duplicate the context sub-tree of the given context source.
	 * 
	 * @param originalSource the original context source
	 * @param duplicate the duplicate source node
	 * @param ignoreCells the cells to be ignored for the duplication
	 */
	public void duplicateContext(SourceNode originalSource, SourceNode duplicate,
			Set<Cell> ignoreCells);

}
