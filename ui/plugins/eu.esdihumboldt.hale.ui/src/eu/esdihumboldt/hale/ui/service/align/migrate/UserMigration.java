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

package eu.esdihumboldt.hale.ui.service.align.migrate;

import java.util.Optional;

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.EntityMatch;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.align.resolver.UserFallbackEntityResolver;
import eu.esdihumboldt.hale.ui.service.align.resolver.internal.EntityCandidates;

/**
 * Migration based on user input.
 * 
 * @author Simon Templer
 */
public class UserMigration implements AlignmentMigration {

	private final SchemaSpaceID schemaSpace;

	/**
	 * Create a new migration. A new instance should be used for every migration
	 * process, as on creation the temporary project settings are reset.
	 * 
	 * @param schemaSpace the schema space
	 */
	public UserMigration(SchemaSpaceID schemaSpace) {
		this.schemaSpace = schemaSpace;

		// reset setting to skip replacements
		UserFallbackEntityResolver.resetSkip();
		// reset replacement cache
		UserFallbackEntityResolver.resetCache();
	}

	@Override
	public Optional<EntityMatch> entityReplacement(EntityDefinition entity,
			TypeDefinition preferRoot, SimpleLog log) {

		// use functionality from entity resolver
		if (entity instanceof TypeEntityDefinition) {
			EntityDefinition candidate = entity;
			Type type = UserFallbackEntityResolver.resolveType((TypeEntityDefinition) entity,
					candidate, schemaSpace);
			return Optional.ofNullable(type).map(e -> EntityMatch.of(e.getDefinition()));
		}
		else if (entity instanceof PropertyEntityDefinition) {
			EntityDefinition candidate = entity;
			candidate = EntityCandidates.find((PropertyEntityDefinition) entity);
			Property property = UserFallbackEntityResolver
					.resolveProperty((PropertyEntityDefinition) entity, candidate, schemaSpace);
			return Optional.ofNullable(property).map(e -> EntityMatch.of(e.getDefinition()));
		}
		else {
			log.error("Unrecognised entity type: " + entity.getClass());
			return Optional.empty();
		}
	}

}
