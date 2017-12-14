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

package eu.esdihumboldt.hale.common.align.merge.impl

import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Condition
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType
import groovy.transform.CompileStatic

/**
 * Alignment migration base class.
 *
 * @author Simon Templer
 */
@CompileStatic
abstract class AbstractMigration implements AlignmentMigration {

	/**
	 * Find a match for the given entity
	 * @param entity the entity
	 * @return the match if found
	 */
	protected abstract Optional<EntityDefinition> findMatch(EntityDefinition entity);

	@Override
	Optional<EntityDefinition> entityReplacement(EntityDefinition entity, SimpleLog log) {
		def defaultEntity = AlignmentUtil.getAllDefaultEntity(entity)

		def matchedEntity = findMatch(defaultEntity)

		// special case handling
		if (!matchedEntity.isPresent()) {
			matchedEntity = findParentMatch(defaultEntity)
			if (matchedEntity.present) {
				println "Inaccurate match of $entity to ${matchedEntity.get()} via parent entity"
			}
		}

		if (matchedEntity.present) {
			// entity contained contexts -> translate them if possible

			if (entity.filter) {
				// apply filter to entity
				//TODO replacements in filter?
				//FIXME mark unsafe

				// add filter to match
				matchedEntity = matchedEntity.map { EntityDefinition match ->
					AlignmentUtil.createEntity(match.type, match.propertyPath,
							SchemaSpaceID.SOURCE, entity.filter)
				}
			}

			if (entity.propertyPath && entity != defaultEntity) {
				// likely a context was present
				matchedEntity = matchedEntity.map { applyContexts(it, entity) }
			}
		}

		if (!matchedEntity.isPresent()) {
			println "No match for entity $entity found"
		}

		return matchedEntity
	}

	private EntityDefinition applyContexts(EntityDefinition entity, EntityDefinition contexts) {
		if (!entity.propertyPath || !contexts.propertyPath) {
			// return unchanged - no properties to adapt
			return entity
		}

		if (entity.propertyPath.size() == 1) {
			// special handling if the property depth is only one

			// prefer first instance context
			Integer contextName = contexts.propertyPath.findResult { it.contextName }
			// prefer first index context
			Integer index = contexts.propertyPath.findResult { it.index }
			// prefer last condition
			Condition condition = contexts.propertyPath.reverse().findResult { it.condition }

			ChildContext pathContext = new ChildContext(contextName, index, condition,
					entity.propertyPath[0].child)

			List<ChildContext> path = [pathContext]

			return new PropertyEntityDefinition(entity.type, path, entity.schemaSpace, entity.filter)
		}
		else {
			// use best guess (top to bottom)
			//FIXME more cases? improve handling

			return DefaultSchemaMigration.applyContexts(entity, contexts)
		}
	}

	protected Optional<EntityDefinition> findParentMatch(EntityDefinition entity) {
		//XXX only allow parent matches for specific cases right now
		if (!(entity.definition instanceof PropertyDefinition) ||
		!((PropertyDefinition) entity.definition).propertyType.getConstraint(GeometryType).isGeometry()) {
			// not a geometry
			return Optional.empty()
		}

		while (entity != null) {
			entity = AlignmentUtil.getParent(entity)

			def matchedEntity = findMatch(entity);
			if (matchedEntity.present) {
				return matchedEntity
			}
		}

		return Optional.empty()
	}

}
