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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;

/**
 * Migration utility methods.
 * 
 * @author Simon Templer
 */
public class MigrationUtil {

	/**
	 * Remove base alignment cells from the mapping by removing the base
	 * alignments. Custom functions will be retained as functions defined
	 * directly in the alignment.
	 * 
	 * @param alignment the alignment to adapt
	 */
	public static void removeBaseCells(MutableAlignment alignment) {
		// collect custom functions defined in base alignments
		Map<String, CustomPropertyFunction> baseFunctions = new HashMap<>(
				alignment.getAllCustomPropertyFunctions());
		alignment.getCustomPropertyFunctions().forEach((id, func) -> {
			baseFunctions.remove(id);
		});

		alignment.clearBaseAlignments();

		// add functions previously defined in base alignments
		baseFunctions.values().forEach(function -> alignment.addCustomPropertyFunction(function));
	}

	/**
	 * Update a cell to remove ID prefixes originating from base alignment IDs.
	 * 
	 * @param cell the cell to update
	 * @param updateId if the cell's own ID should be updated
	 * @param updateDisabledFor if the IDs of the cells the cell is disabled for
	 *            should be updated
	 */
	public static void removeIdPrefix(MutableCell cell, boolean updateId,
			boolean updateDisabledFor) {
		// update cell ID
		if (updateId) {
			String cellId = stripPrefix(cell.getId());
			cell.setId(cellId);
		}

		// update disabled for
		if (updateDisabledFor) {
			Set<String> disabledFor = new HashSet<>(cell.getDisabledFor());
			disabledFor.forEach(disabledId -> {
				String strippedId = stripPrefix(disabledId);
				if (!strippedId.equals(disabledId)) {
					cell.setDisabledFor(disabledId, false);
					cell.setDisabledFor(strippedId, true);
				}
			});
		}
	}

	private static String stripPrefix(String id) {
		if (id == null) {
			return null;
		}

		int sepIndex = id.indexOf(':');
		if (sepIndex >= 0 && id.length() > sepIndex + 1) {
			return id.substring(sepIndex + 1);
		}
		return id;
	}

	/**
	 * Determines if the given cell is a direct match.
	 * 
	 * @param match the cell to test
	 * @return <code>true</code> if the cell represents a direct match,
	 *         <code>false</code> otherwise
	 */
	public static boolean isDirectMatch(Cell match) {
		return match.getTransformationIdentifier().equals(RetypeFunction.ID)
				|| match.getTransformationIdentifier().equals(RenameFunction.ID);
	}

}
