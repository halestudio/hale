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

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigrator;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.migrate.MigrationOptions;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;

/**
 * Default implementation of migrator for a complete alignment.
 * 
 * @author Simon Templer
 */
public class DefaultAlignmentMigrator implements AlignmentMigrator {

	private final CellMigrator defaultMigrator = new DefaultCellMigrator();

	@Override
	public MutableAlignment updateAligmment(Alignment originalAlignment,
			AlignmentMigration migration, MigrationOptions options) {
		MutableAlignment result = new DefaultAlignment(originalAlignment);

		// XXX TODO adapt custom functions?!
//		result.getCustomPropertyFunctions();

		for (Cell cell : result.getCells()) {
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
			}
		}

		return result;
	}

	private CellMigrator getCellMigrator(String transformationIdentifier) {
		// TODO Auto-generated method stub
		return defaultMigrator;
	}

}
