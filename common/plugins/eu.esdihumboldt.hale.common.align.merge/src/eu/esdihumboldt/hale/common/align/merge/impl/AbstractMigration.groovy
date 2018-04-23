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
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
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
		EntityDefinition defaultEntity = AlignmentUtil.getAllDefaultEntity(entity)
		Optional<EntityDefinition> matchedEntity = findMatch(defaultEntity)

		// special case handling
		if (!matchedEntity.isPresent()) {
			matchedEntity = findParentMatch(defaultEntity)
			if (matchedEntity.present) {
				log.warn "Inaccurate match of $entity to ${matchedEntity.get()} via parent entity"
			}
		}

		if (matchedEntity.present) {
			// entity contained contexts -> translate them if possible

			matchedEntity = Optional.ofNullable(translateContexts(entity, matchedEntity.get(), log));
		}

		if (!matchedEntity.isPresent()) {
			log.error("No match for entity $entity found")
		}

		return matchedEntity
	}

	static EntityDefinition translateContexts(EntityDefinition original, EntityDefinition target, SimpleLog log) {
		def defaultEntity = AlignmentUtil.getAllDefaultEntity(original)

		if (original.filter) {

			// what about if match has filter?
			if (target.filter && original.filter != target.filter) {
				def filterString = FilterDefinitionManager.instance.asString(original.filter)
				def msg = "Filter condition applied to the original source type has been dropped because a filter already existed for the entity it was replaced with. Please check if you need to change the condition to match both original conditions."
				if (filterString) {
					msg = msg + " The filter on the original source ${original} was \"${filterString}\"."
				}
				log.warn(msg)
			}
			else {
				// apply filter to entity
				//TODO replacements in filter?
				// mark unsafe if entity is not the same
				if (!sameEntity(original, target)) {
					log.warn("Filter condition may not be valid because the entity it is applied to has been replaced")
				}

				// add filter to match
				target = AlignmentUtil.createEntity(target.type, target.propertyPath,
						SchemaSpaceID.SOURCE, original.filter)
			}
		}

		if (original.propertyPath && original != defaultEntity) {
			// likely a context was present
			target = applyContexts(target, original, log)
		}

		return target
	}

	/**
	 * Check if entities are the same, ignoring contexts.
	 * @param e1 the first entity to check
	 * @param e2 the second entity to check
	 * @return <code>true</code> if the entities are the same, <code>false</code> if not
	 */
	private static boolean sameEntity(EntityDefinition e1, EntityDefinition e2) {
		// FIXME rather do a structural check! for filter it matches if the structure changes

		if (!e1.type.equals(e2.type)) {
			return false
		}

		// property path
		if (e1.propertyPath.size() != e2.propertyPath.size()) {
			return false
		}
		for (int i = 0; i < e1.propertyPath.size(); i++) {
			ChildContext child1 = e1.propertyPath[i]
			ChildContext child2 = e2.propertyPath[i]

			if (child1.child != child2.child) {
				return false
			}
		}

		return true;
	}

	private static EntityDefinition applyContexts(EntityDefinition entity, EntityDefinition contexts, SimpleLog log) {
		if (!entity.propertyPath || !contexts.propertyPath) {
			// return unchanged - no properties to adapt
			return entity
		}

		if (entity.propertyPath.size() == 1) {
			// special handling if the property depth is only one

			// check existing context from match
			//XXX also check index? name?
			Condition matchCondition = entity.propertyPath[0].condition

			// prefer first instance context
			Integer contextName = contexts.propertyPath.findResult { it.contextName }
			// prefer first index context
			Integer index = contexts.propertyPath.findResult { it.index }
			// prefer last condition
			Condition condition = contexts.propertyPath.reverse().findResult { it.condition }

			// decide whether to use match condition or context condition
			if (matchCondition?.filter) {
				// keep match condition

				if (condition?.filter && matchCondition.filter != condition.filter) {
					def filterString = FilterDefinitionManager.instance.asString(condition.filter)
					def msg = "Filter condition applied to the original source has been dropped because a filter already existed for the entity it was replaced with. Please check if you need to change the condition to match both original conditions."
					if (filterString) {
						msg = msg + " The filter on the original source ${entity} was \"${filterString}\"."
					}
					log.warn(msg)
				}

				condition = matchCondition
			}
			else if (condition?.filter) {
				// mark unsafe if entity is not the same
				if (!sameEntity(entity, contexts)) {
					log.warn("Filter condition may not be valid because the entity it is applied to has been replaced")
				}
			}

			ChildContext pathContext = new ChildContext(contextName, index, condition,
					entity.propertyPath[0].child)

			List<ChildContext> path = [pathContext]

			return new PropertyEntityDefinition(entity.type, path, entity.schemaSpace, entity.filter)
		}
		else {
			// use best guess (top to bottom)
			//FIXME more cases? improve handling

			//FIXME will replace all match contexts!
			//XXX though not critical for XtraServer use case because there is only one level of properties

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
