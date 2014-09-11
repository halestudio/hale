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

import eu.esdihumboldt.hale.common.align.io.impl.dummy.EntityToDef
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * Helper methods for finding candidates for entity definitions that cannot be resolved.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
@CompileStatic
class EntityCandidates {

	public static boolean isCandidate(ClassType.Type clazz, TypeDefinition type) {
		type.name.localPart == clazz.name
	}

	/**
	 * Get a candidate for resolving the given property entity.
	 * 
	 * @param entity the entity to resolve
	 * @return an entity definition candidate or <code>null</code>
	 */
	public static EntityDefinition find(PropertyType entity, TypeIndex schema, SchemaSpaceID schemaSpace) {
		EntityDefinition result = null
		schema.types.findAll(EntityCandidates.&isCandidate.curry(entity.type)).find { TypeDefinition typeCand ->
			// each type candidate
			EntityDefinition typeDef = EntityToDef.toDef(entity.type, typeCand, schemaSpace)

			// check if there is a candidate for the children
			result = findChildren(typeDef, entity)
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
		// find a type with the same local name
		TypeDefinition type = schema.types.find(EntityCandidates.&isCandidate.curry(entity.type))

		if (type) {
			EntityToDef.toDef(entity.type, type, schemaSpace)
		}
		else {
			null
		}
	}
}
