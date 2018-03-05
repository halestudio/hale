/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.align.merge;

import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * Index of cells for an alignment that is used for a Merge.
 * 
 * @author Simon Templer
 */
public interface MergeIndex {

	/**
	 * Get cells with a specific target. The target is usually a source of a
	 * cell that is to be migrated.
	 * 
	 * @param def the entity definition of the target
	 * @return the cells have the given target, conditions or other contexts on
	 *         the entity are ignored
	 */
	List<Cell> getCellsForTarget(EntityDefinition def);

}
