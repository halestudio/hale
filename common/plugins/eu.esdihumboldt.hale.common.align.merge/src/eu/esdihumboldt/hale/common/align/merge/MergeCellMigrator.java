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

import java.util.function.Function;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.CellMigrator;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;

/**
 * Interface for migration of single cells based on a {@link MergeIndex}.
 * 
 * @author Simon Templer
 */
public interface MergeCellMigrator {

	/**
	 * Update a cell as part of an alignment merge. The target of the cell stays
	 * the same while source and parameters are update.
	 * 
	 * @param originalCell the original cell
	 * @param mergeIndex the merge index
	 * @param migration the alignment migration (may be useful for cases where
	 *            only entity replacement needs to be done)
	 * @param getCellMigrator functions that yields a cell migrator for a
	 *            function (may be useful for cases where only entity
	 *            replacement needs to be done)
	 * @param log the migration process log
	 * @return the updated cell or cells
	 */
	Iterable<MutableCell> mergeCell(Cell originalCell, MergeIndex mergeIndex,
			AlignmentMigration migration, Function<String, CellMigrator> getCellMigrator,
			SimpleLog log);

}
