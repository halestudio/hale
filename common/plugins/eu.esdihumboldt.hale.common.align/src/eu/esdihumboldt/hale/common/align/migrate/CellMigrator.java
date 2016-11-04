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

package eu.esdihumboldt.hale.common.align.migrate;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableCell;

/**
 * Interface for implementing migration of single cells based on an
 * {@link AlignmentMigration}.
 * 
 * @author Simon Templer
 */
public interface CellMigrator {

	/**
	 * Update a cell as part of an alignment migration.
	 * 
	 * @param originalCell the original cell
	 * @param migration the alignment migration
	 * @param options the migration options
	 * @return the updated cell
	 */
	MutableCell updateCell(Cell originalCell, AlignmentMigration migration,
			MigrationOptions options);

}
