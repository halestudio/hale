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

package eu.esdihumboldt.hale.common.align.merge.impl;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.merge.MergeIndex;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;

/**
 * @author Simon Templer
 */
public class TargetIndex implements MergeIndex {

	private final ListMultimap<EntityDefinition, Cell> cells;

	/**
	 * Create a new index based on an alignment.
	 * 
	 * @param alignment the alignment
	 */
	public TargetIndex(Alignment alignment) {
		cells = ArrayListMultimap.create();

		for (Cell cell : alignment.getCells()) {
			Entity target = CellUtil.getFirstEntity(cell.getTarget());
			if (target != null) {
				EntityDefinition targetDef = AlignmentUtil
						.getAllDefaultEntity(target.getDefinition(), true);
				cells.put(targetDef, cell);
			}
		}
	}

	@Override
	public List<Cell> getCellsForTarget(EntityDefinition def) {
		return Collections
				.unmodifiableList(cells.get(AlignmentUtil.getAllDefaultEntity(def, true)));
	}

}
