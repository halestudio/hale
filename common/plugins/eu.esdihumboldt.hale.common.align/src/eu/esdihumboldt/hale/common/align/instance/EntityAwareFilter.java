/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.hale.common.align.instance;

import java.util.List;
import java.util.Optional;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Extended filter interface for filters aware of referenced entities.
 * 
 * @author Simon Templer
 */
public interface EntityAwareFilter extends Filter {

	/**
	 * Get the entities references by the filter, given the provided context.
	 * 
	 * @param context the filter entity context
	 * @return the list of referenced entities, for each distinct variable
	 *         encountered in the filter there should be an entry, if may be
	 *         empty if the reference cannot be resolved in the given context
	 */
	List<Optional<EntityDefinition>> getReferencedEntities(EntityDefinition context);

	/**
	 * States if the filter supports migration via
	 * {@link #migrateFilter(EntityDefinition, AlignmentMigration, TypeDefinition, SimpleLog)}
	 * 
	 * @return <code>true</code> if migration is supported, <code>false</code>
	 *         otherwise
	 */
	boolean supportsMigration();

	/**
	 * Migrate the filter based on the given entity context and alignment
	 * migration.
	 * 
	 * @param context the entity context
	 * @param migration the alignment migration
	 * @param preferRoot hint on which entity to prefer if there are multiple
	 *            matches
	 * @param log the operation log
	 * @return the migrated filter, if migration is possible
	 */
	Optional<Filter> migrateFilter(EntityDefinition context, AlignmentMigration migration,
			TypeDefinition preferRoot, SimpleLog log);

}
