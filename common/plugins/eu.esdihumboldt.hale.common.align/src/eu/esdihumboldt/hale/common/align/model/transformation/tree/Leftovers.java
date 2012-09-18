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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.util.Pair;

/**
 * Represents additional values not represented in their own source node or not
 * processed for all associated cells.
 * 
 * @author Simon Templer
 */
public interface Leftovers {

	/**
	 * Consume a left over value completely (for all remaining cells). The
	 * returned source node associated as annotated child to the parent of the
	 * original source node.
	 * 
	 * @return the source node created for the value paired with the set of
	 *         cells that have already been consumed for the value, or
	 *         <code>null</code> if there is no value that hasn't been consumed
	 *         completely
	 */
	Pair<SourceNode, Set<Cell>> consumeValue();

	/**
	 * Consume a left over value regarding the given cell. The returned source
	 * node associated as annotated child to the parent of the original source
	 * node.
	 * 
	 * @param cell the cell
	 * @return the source node created for the value or <code>null</code> if
	 *         there is no value that is not yet consumed completely or for the
	 *         given cell
	 */
	SourceNode consumeValue(Cell cell);

}
