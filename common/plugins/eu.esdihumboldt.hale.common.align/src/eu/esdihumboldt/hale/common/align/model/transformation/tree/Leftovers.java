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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.util.Pair;

/**
 * Represents additional values not represented in their own source node or
 * not processed for all associated cells.
 * @author Simon Templer
 */
public interface Leftovers {
	
	/**
	 * Consume a left over value completely (for all remaining cells).
	 * The returned source node associated as annotated child to the parent
	 * of the original source node.
	 * @return the source node created for the value paired with the set of 
	 *   cells that have already been consumed for the value, or
	 *   <code>null</code> if there is no value that hasn't been consumed
	 *   completely
	 */
	Pair<SourceNode, Set<Cell>> consumeValue();
	
	/**
	 * Consume a left over value regarding the given cell.
	 * The returned source node associated as annotated child to the parent
	 * of the original source node.
	 * @param cell the cell
	 * @return the source node created for the value or <code>null</code> if
	 *   there is no value that is not yet consumed completely or for the given
	 *   cell
	 */
	SourceNode consumeValue(Cell cell);

}
