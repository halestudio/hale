/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.migrate.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;

/**
 * Helper that converts an alignment into an expanded version where all
 * effective mappings are defined explicitly, i.e. mappings effective due to
 * inheritance are converted to use the respective sub-types. Also, base
 * alignment cells are converted into normal cells.
 * 
 * @author Simon Templer.
 */
public class EffectiveMapping {

	/**
	 * Convert an alignment into an expanded version where all effective
	 * mappings are defined explicitly, i.e. mappings effective due to
	 * inheritance are converted to use the respective sub-types. Also, base
	 * alignment cells are converted into normal cells.
	 * 
	 * @param alignment the alignment to convert
	 * @return the expanded copy of the alignment
	 */
	public static MutableAlignment expand(Alignment alignment) {
		MutableAlignment result = new DefaultAlignment(alignment);

		// remove base alignment cells keeping custom functions
		MigrationUtil.removeBaseCells(result);
		// remove other cells
		result.clearCells();

		// transfer cells based on effective mapping

		// set of all cells used as they are in the resulting alignment
		Set<Cell> usedAsIs = new HashSet<>();

		for (Cell typeCell : alignment.getTypeCells()) {
			// transfer type cell unchanged
			MutableCell typeCellNew = new DefaultCell(typeCell);
			result.addCell(typeCellNew);
			usedAsIs.add(typeCell);

			Collection<? extends Cell> propertyCells = alignment.getPropertyCells(typeCell, true,
					false);
			for (Cell propertyCell : propertyCells) {
				Cell reparented = AlignmentUtil.reparentCell(propertyCell, typeCell, true);
				if (reparented == propertyCell) {
					// use as is
					if (!usedAsIs.contains(propertyCell)) {
						// only add if not done yet
						// transfer unchanged
						MutableCell newCell = new DefaultCell(propertyCell);
						result.addCell(newCell);
						usedAsIs.add(propertyCell);
					}
				}
				else {
					// inherited cell

					// add the reparented cell
					// TODO check if similar cell has been added already?
					result.addCell((MutableCell) reparented);
				}
			}
		}

		return result;
	}

}
