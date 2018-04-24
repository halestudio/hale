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

import javax.annotation.Nullable
import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.align.groovy.accessor.EntityAccessor
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.core.report.SimpleLog
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import groovy.transform.CompileStatic

/**
 * Default migration implementation supporting basic migration functionality like replacing namespaces.
 *
 * @author Simon Templer
 */
@CompileStatic
class DefaultSchemaMigration implements AlignmentMigration {

	final SchemaSpace newSchema

	DefaultSchemaMigration(SchemaSpace newSchema) {
		this.newSchema = newSchema
	}

	@Override
	public Optional<EntityDefinition> entityReplacement(EntityDefinition entity, SimpleLog log) {

		// default behavior - try to find entity in new schema, based on names w/o namespace

		QName typeName = entity.type.getName()
		TypeDefinition type = findType(typeName, log)
		if (!type) {
			//FIXME
			log.warn "No type match for $entity found"
			return Optional.empty()
		}

		TypeEntityDefinition typeEntity = new TypeEntityDefinition(type, entity.schemaSpace, entity.filter)

		if (entity.propertyPath.empty) {
			// type entity definition - yield
			return Optional.<EntityDefinition>of(typeEntity)
		}
		else {
			// property entity definition -> follow the path

			EntityAccessor acc = new EntityAccessor(typeEntity)

			// descend
			entity.propertyPath.each {
				acc.findChildren(it.child.name.localPart)
			}
			//TODO check if unique?
			EntityDefinition candidate = acc.toEntityDefinition()

			if (!candidate) {
				// if no candidate was found, try again ignoring groups
				acc = new EntityAccessor(typeEntity)

				// descend
				entity.propertyPath.each {
					if (it.child.asProperty()) {
						acc.findChildren(it.child.name.localPart)
					}
				}
				//TODO check if unique?
				candidate = acc.toEntityDefinition()
			}

			if (!candidate) {
				// no match found
				//FIXME
				log.warn "No match for $entity found"
				return Optional.empty()
			}
			else {
				return Optional.ofNullable(applyContexts(candidate, entity))
			}
		}
	}

	@Nullable
	private TypeDefinition findType(QName typeName, SimpleLog log) {
		// check if same name actually exists
		TypeDefinition typeDef = newSchema.getType(typeName)

		if (!typeDef) {
			// search in mapping relevant types
			def candidates = newSchema.mappingRelevantTypes.findAll {
				it.name.localPart == typeName.localPart
			}
			if (!candidates) {
				// search in all types
				candidates = newSchema.types.findAll {
					it.name.localPart == typeName.localPart
				}
			}

			if (candidates) {
				if (candidates.size() > 1) {
					//FIXME how to react?
					log.warn "Multiple matches for type $typeName - $candidates"
				}

				typeDef = (TypeDefinition) candidates[0]
			}
		}

		typeDef
	}

	/**
	 * Apply contexts (in the property path) from a context entity to another entity.
	 *
	 * @param entity the entity to apply the contexts to
	 * @param contexts the entity which contexts to apply to the other entity
	 * @return the entity with the contexts applied based on a best guess
	 */
	public static EntityDefinition applyContexts(EntityDefinition entity, EntityDefinition contexts) {
		if (!entity.propertyPath || !contexts.propertyPath) {
			// return unchanged - no properties to adapt
			return entity
		}

		def checkContexts = new LinkedList<>(contexts.propertyPath)
		List<ChildContext> path = []

		for (int index = 0; index < entity.propertyPath.size(); index++) {
			ChildContext current = entity.propertyPath[index]

			ChildContext candidate = findContext(current, checkContexts)
			if (candidate == current) {
				path << current
			}
			else {
				// recreate context
				ChildContext copy = new ChildContext(candidate.contextName,
						candidate.index, candidate.condition, current.child)
				path << copy
			}
		}

		new PropertyEntityDefinition(entity.type, path, entity.schemaSpace, entity.filter)
	}

	@Nullable
	private static ChildContext findContext(ChildContext current, Deque<ChildContext> relatedPath) {
		if (relatedPath.empty) {
			return current // not context information that is retainable
		}

		ChildContext candidate = relatedPath.pop()

		if (current.child.asGroup() && candidate.child.asGroup()) {
			// if both are groups, assume a match
			return candidate
		}

		while (!relatedPath.empty && candidate.child.asGroup()) {
			// skip groups if the current context is not a group
			candidate = relatedPath.pop()
		}

		candidate
	}

}
