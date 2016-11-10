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

package eu.esdihumboldt.hale.common.align.migrate.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.custom.CustomPropertyFunction;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigrator;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Default implementation of migrator for a complete alignment.
 * 
 * @author Simon Templer
 */
public class DefaultAlignmentMigrator implements AlignmentMigrator {

	private final CellMigrator defaultMigrator = new DefaultCellMigrator();

	private final ServiceProvider serviceProvider;

	/**
	 * @param serviceProvider the service provider if available
	 */
	public DefaultAlignmentMigrator(@Nullable ServiceProvider serviceProvider) {
		super();
		this.serviceProvider = serviceProvider;
	}

	@Override
	public MutableAlignment updateAligmment(Alignment originalAlignment,
			AlignmentMigration migration, MigrationOptions options) {
		MutableAlignment result = new DefaultAlignment(originalAlignment);

		// XXX TODO adapt custom functions?!
//		result.getCustomPropertyFunctions();

		Collection<? extends Cell> cellList = new ArrayList<>(result.getCells());
		for (Cell cell : cellList) {
			// XXX
			if (cell instanceof MutableCell) {
				CellMigrator cm = getCellMigrator(cell.getTransformationIdentifier());
				MutableCell newCell = cm.updateCell(cell, migration, options);
				result.removeCell(cell);
				if (newCell != null) {
					result.addCell(newCell);
				}
			}
			else {
				// XXX can we deal with other cases? (Base alignment cells)
				if (options.transferBase()) {
					// include base alignment cell as mutable mapping cell
					CellMigrator cm = getCellMigrator(cell.getTransformationIdentifier());
					MutableCell newCell = cm.updateCell(cell, migration, options);
					result.removeCell(cell);
					if (newCell != null) {
						result.addCell(newCell);
					}
				}
			}
		}

		if (options.transferBase()) {
			// collect custom functions defined in base alignments
			Map<String, CustomPropertyFunction> baseFunctions = new HashMap<>(
					result.getAllCustomPropertyFunctions());
			result.getCustomPropertyFunctions().forEach((id, func) -> {
				baseFunctions.remove(id);
			});

			result.clearBaseAlignments();

			// add functions previously defined in base alignments
			baseFunctions.values().forEach(function -> result.addCustomPropertyFunction(function));
		}
		else {
			// does something need to be done to correctly retain base
			// alignments?
		}

		return result;
	}

	private CellMigrator getCellMigrator(String transformationIdentifier) {
		return FunctionUtil.getFunction(transformationIdentifier, serviceProvider)
				.getCustomMigrator().orElse(defaultMigrator);
	}

}
