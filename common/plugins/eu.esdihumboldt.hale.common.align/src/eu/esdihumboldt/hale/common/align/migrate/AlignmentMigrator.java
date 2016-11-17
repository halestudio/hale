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

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;

/**
 * Interface for implementing migration of a complete alignment based on an
 * {@link AlignmentMigration}.
 * 
 * @author Simon Templer
 */
public interface AlignmentMigrator {

	/**
	 * Update an alignment as part of an alignment migration.
	 * 
	 * @param originalAlignment the original alignment
	 * @param migration the alignment migration
	 * @param options the migration options
	 * @return the updated alignment
	 */
	MutableAlignment updateAligmment(Alignment originalAlignment, AlignmentMigration migration,
			MigrationOptions options);

}
