/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.html.svg.mapping.json

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ConditionType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import groovy.transform.CompileStatic

/**
 * JSON format for Alignment based on the JAXB model types.
 * The JAXB model types are used as a basis because they may be used
 * independent of if a loaded schema is present (it is linked to
 * schema definitions only via name references).
 * 
 * Therefore must be updated/adapted to changes to the HALE Alignment schema.
 * 
 * Implemented to be as close as possible to the format for cells in the
 * mapping documentation and the schema JSON representation.
 * 
 * Previous attempts to use the JAXB bindings with Jackson or serializing to
 * JSON using EclipseLink MOXy did not prove to the usable in this case.
 * (No support for JAXBElement in one case, duplicate property names in the
 * other)
 */
@CompileStatic
class JaxbAlignmentJson {

	private static def of = new ObjectFactory()

	private static final String CD_LANGUAGE = 'lang'
	private static final String CD_LANGUAGE_DEF = 'CQL'
	private static final String CD_EXPRESSION = 'expression'

	void writeCondition(JsonStreamBuilder json, ConditionType condition) {
		json {
			json CD_LANGUAGE, condition.lang ?: CD_LANGUAGE_DEF
			json CD_EXPRESSION, condition.value
		}
	}

	private static final String QN_NAME = 'localName'
	private static final String QN_NAMESPACE = 'namespace'
	private static final String ENTITY_CONDITION = 'condition'

	void writeType(JsonStreamBuilder json, ClassType.Type type) {
		json {
			json QN_NAME, type.name
			json QN_NAMESPACE, type.ns
			if (type.condition) {
				json ENTITY_CONDITION, {
					writeCondition(json, type.condition)
				}
			}
		}
	}

	private static final String ENTITY_CONTEXT = 'context'
	private static final String ENTITY_INDEX = 'index'

	void writeChildContext(JsonStreamBuilder json, ChildContextType childContext) {
		json {
			json QN_NAME, childContext.name
			json QN_NAMESPACE, childContext.ns
			if (childContext.condition) {
				json ENTITY_CONDITION, {
					writeCondition(json, childContext.condition)
				}
			}
			if (childContext.context != null) {
				json ENTITY_CONTEXT, childContext.context
			}
			if (childContext.index != null) {
				json ENTITY_INDEX, childContext.index
			}
		}
	}

	private static final String ENTITY_TYPE = 'type'
	private static final String ENTITY_PATH = 'path'

	void writeEntity(JsonStreamBuilder json, ClassType entity) {
		json {
			if (entity instanceof PropertyType) {
				json ENTITY_TYPE, {
					writeType(json, entity.type)
				}
				entity.child.each { child ->
					json ENTITY_PATH, true, { writeChildContext(json, child) }
				}
			}
			else {
				json ENTITY_TYPE, {
					writeType(json, entity.type)
				}
			}
		}
	}

	private static final String ENTITY_NAME = "name"
	private static final String ENTITY_CLASS = "class"
	private static final String ENTITY_PROPERTY = "property"

	void writeNamedEntity(JsonStreamBuilder json, NamedEntityType namedEntity) {
		boolean isProperty = namedEntity.abstractEntity?.value instanceof PropertyType
		ClassType entity
		if (namedEntity.abstractEntity?.value) {
			entity = namedEntity.abstractEntity.value as ClassType
		}

		json {
			json ENTITY_NAME, namedEntity.name
			if (isProperty) {
				json ENTITY_PROPERTY, { writeEntity(json, entity) }
			}
			else {
				json ENTITY_CLASS, { writeEntity(json, entity) }
			}
		}
	}
}
