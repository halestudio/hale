/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.align.resolver.internal

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.align.io.impl.dummy.EntityToDef
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import eu.esdihumboldt.hale.ui.service.align.resolver.UserFallbackEntityResolver
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * Helper methods for finding candidates for entity definitions that cannot be resolved.
 * 
 * @author Simon Templer
 */
@CompileStatic
class EntityCandidates {

	private static final ALogger log = ALoggerFactory.getLogger(EntityCandidates)

	public static boolean isCandidate(TypeEntityDefinition dummy, TypeDefinition type) {
		boolean isCandidate = type.name.localPart == dummy.type.name.localPart
		if (!isCandidate) {
			def replacement = UserFallbackEntityResolver.getCache().getReplacement(dummy)
			if (replacement) {
				isCandidate = replacement.definition == type
			}
		}
		isCandidate
	}

	/**
	 * Get a candidate for resolving the given property entity.
	 * 
	 * @param entity the entity to resolve
	 * @return an entity definition candidate or <code>null</code>
	 */
	public static EntityDefinition find(PropertyType entity, TypeIndex schema, SchemaSpaceID schemaSpace) {
		EntityDefinition result = null

		try {
			// first try with type replacement
			TypeEntityDefinition dummy = EntityToDef.toDummyDef(entity.type, schemaSpace)
			TypeEntityDefinition typeReplacement = UserFallbackEntityResolver.getCache().getReplacement(dummy)
			if (typeReplacement) {
				result = findChildren(typeReplacement, entity)
			}

			if (!result) {
				schema.types.findAll(EntityCandidates.&isCandidate.curry(dummy)).find { TypeDefinition typeCand ->
					// each type candidate
					TypeEntityDefinition typeDef = EntityToDef.toDef(entity.type, typeCand, schemaSpace)

					// check if there is a candidate for the children
					result = findChildren(typeDef, entity)
				}
			}
		} catch (Exception e) {
			log.error('Error looking for entity definition resolve candidate', e);
		}

		result
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	static EntityDefinition findChildren(EntityDefinition typeDef, PropertyType entity) {
		def accessor = typeDef.accessor()
		entity.child.each { ChildContextType cct ->
			// access by name, ignore namespace, copy context information
			accessor = accessor."$cct.name"(null, cct)
		}
		accessor.toEntityDefinition()
	}

	/**
	 * Get a candidate for resolving the given class entity.
	 * 
	 * @param entity the entity to resolve
	 * @return an entity definition candidate or <code>null</code>
	 */
	public static EntityDefinition find(ClassType entity, TypeIndex schema, SchemaSpaceID schemaSpace) {
		TypeEntityDefinition dummy = EntityToDef.toDummyDef(entity.type, schemaSpace)

		// check if there is a replacement in the cache
		def replacement = UserFallbackEntityResolver.getCache().getReplacement(dummy)
		if (replacement) {
			return replacement
		}

		// find a type with the same local name
		TypeDefinition type = schema.types.find(EntityCandidates.&isCandidate.curry(dummy))

		if (type) {
			EntityToDef.toDef(entity.type, type, schemaSpace)
		}
		else {
			null
		}
	}

	/**
	 * Get a candidate for resolving the given property entity using the resolver cache.
	 *
	 * @param entity the entity to resolve
	 * @return an entity definition candidate or <code>null</code>
	 */
	public static PropertyEntityDefinition find(PropertyEntityDefinition original) {
		PropertyEntityDefinition result = null

		try {
			// first try with type replacement
			TypeEntityDefinition typeReplacement = UserFallbackEntityResolver.getCache().getReplacement(AlignmentUtil.getTypeEntity(original))
			if (typeReplacement) {
				result = (PropertyEntityDefinition) findChildren(typeReplacement, original)
			}
		} catch (Exception e) {
			log.error('Error looking for entity definition resolve candidate', e);
		}

		if (!result) {
			result = original
		}

		result
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	static EntityDefinition findChildren(EntityDefinition typeDef, PropertyEntityDefinition property) {
		def accessor = typeDef.accessor()
		property.propertyPath.each { ChildContext cct ->
			// access by name, ignore namespace, copy context information
			accessor = accessor."${cct.child.name.localPart}"(null, cct)
		}
		accessor.toEntityDefinition()
	}
}
