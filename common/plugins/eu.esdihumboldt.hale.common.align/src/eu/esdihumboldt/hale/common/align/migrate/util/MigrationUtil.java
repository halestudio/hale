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
import java.util.Map;

import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;

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

}
