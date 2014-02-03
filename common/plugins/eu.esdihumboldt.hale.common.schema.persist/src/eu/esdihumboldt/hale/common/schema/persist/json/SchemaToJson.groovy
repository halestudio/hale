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

package eu.esdihumboldt.hale.common.schema.persist.json

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import groovy.transform.CompileStatic


/**
 * Creates a JSON representation for visualization of the HALE Schema Model.
 * 
 * @author Simon Templer
 */
@CompileStatic
class SchemaToJson {

	/**
	 * Create a JSON representation of the given set of schemas.
	 * 
	 * @param writer the writer to write the JSON to
	 * @param schemas the schemas to serialize
	 */
	static void schemasToJson(Writer writer, def schemas) {
		JsonStreamBuilder json = new JsonStreamBuilder(writer, true)
		List<Element> elements = []
		json {
			// tree nodes
			schemas.each { Schema schema ->
				json 'treeNodes[]', {
					schemaToJson(json, schema, elements)
				}
			}
			// elements
			elements.each { Element ele ->
				json 'elements[]', { ele.toJson(json) }
			}
			//TODO associations
		}
	}

	static void schemaToJson(JsonStreamBuilder json, Schema schema, List<Element> elements) {
		json {
			json 'nodeLabel', schema.namespace
			json 'nodeQName', schema.namespace
			json 'nodeType', 'package'

			// list to collect references to types
			def typeIndices = []

			// subnodes (= types)
			schema.mappingRelevantTypes.each { TypeDefinition type ->
				json 'subNodes[]', {
					typeToJson(json, type, elements)
				}

				// create element representing type
				Element element = new Element(label: type.displayName, name: type.name)
				if (type.description) {
					element.description = type.description
				}

				// add to elements, remember index
				elements << element
				typeIndices << elements.size() - 1
			}

			json 'nodeElements', typeIndices
		}
	}

	static void typeToJson(JsonStreamBuilder json, TypeDefinition type, List<Element> elements) {
		json {
			json 'nodeLabel', type.displayName
			json 'nodeQName', type.name as String
			json 'nodeType', 'class'

			childrenToJson(json, type.children, elements)
		}
	}

	static void groupToJson(JsonStreamBuilder json, GroupPropertyDefinition group, List<Element> elements) {
		json {
			json 'nodeLabel', group.displayName
			json 'nodeQName', group.name as String
			json 'nodeType', 'group'

			childrenToJson(json, group.declaredChildren, elements)
		}
	}

	static void childrenToJson(JsonStreamBuilder json, Iterable<? extends ChildDefinition> children, List<Element> elements) {
		json {
			// list to collect references to children
			def childIndices = []

			// subnodes (= groups) & elements
			children.each { ChildDefinition child ->
				// create element representing child
				Element element = new Element(label: child.displayName, name: child.name)
				if (child.description) {
					element.description = child.description
				}

				if (child.asProperty() != null) {
					// child is a property
					element.typeName = child.asProperty().propertyType?.name
				}

				if (child.asGroup() != null) {
					json 'subNodes[]', {
						groupToJson(json, child.asGroup(), elements)
					}
				}

				// add to elements, remember index
				elements << element
				childIndices << elements.size() - 1
			}

			json 'nodeElements', childIndices
		}
	}

}
