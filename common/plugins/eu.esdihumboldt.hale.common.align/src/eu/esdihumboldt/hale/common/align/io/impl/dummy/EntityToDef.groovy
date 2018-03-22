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

package eu.esdihumboldt.hale.common.align.io.impl.dummy

import javax.xml.namespace.QName

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractEntityType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Condition
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import eu.esdihumboldt.hale.common.instance.model.Filter
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import groovy.transform.TypeChecked


/**
 * Creates (dummy) entity definitions for {@link AbstractEntityType}s.
 * 
 * @author Simon Templer
 */
@TypeChecked
class EntityToDef {

	/**
	 * Postfix for display names for dummy types and properties. 
	 */
	private static final String NAME_POSTFIX = '*'

	static EntityDefinition toDummyDef(AbstractEntityType entity, SchemaSpaceID schemaSpace) {
		// delegate to concrete methods
		toDummyDef(entity, schemaSpace)
	}

	static TypeEntityDefinition toDummyDef(ClassType clazz, SchemaSpaceID schemaSpace) {
		toDummyDef(clazz.type, schemaSpace)
	}

	static TypeEntityDefinition toDummyDef(ClassType.Type clazz, SchemaSpaceID schemaSpace) {
		DefaultTypeDefinition typeDef = new DefaultTypeDefinition(new QName(clazz.ns, clazz.name))
		typeDef.setConstraint(new DisplayName(clazz.name + NAME_POSTFIX))
		toDef(clazz, typeDef, schemaSpace)
	}

	static TypeEntityDefinition toDef(ClassType.Type clazz, TypeDefinition type, SchemaSpaceID schemaSpace) {
		Filter filter = null
		if (clazz.condition) {
			filter = FilterDefinitionManager.instance.from(clazz.condition.lang, clazz.condition.value)
		}

		new TypeEntityDefinition(type, schemaSpace, filter)
	}

	static PropertyEntityDefinition toDummyDef(PropertyType property, SchemaSpaceID schemaSpace) {
		TypeEntityDefinition typeEntity = toDummyDef(property.type, schemaSpace)

		DefaultTypeDefinition typeDef = (DefaultTypeDefinition) typeEntity.definition
		DefaultTypeDefinition parentType = typeDef

		List<ChildContext> path = []

		property.child.eachWithIndex { ChildContextType cct, int index ->
			// create child for parent type
			DefaultTypeDefinition propertyType = new DefaultTypeDefinition(new QName("UnknownPropertyType_${index}"))
			DefaultPropertyDefinition propDef = new DefaultPropertyDefinition(new QName(cct.ns, cct.name), parentType, propertyType)
			propDef.setConstraint(new DisplayName(cct.name + NAME_POSTFIX))

			// create child context
			Condition condition = null
			if (cct.condition?.value) {
				Filter filter = FilterDefinitionManager.instance.from(cct.condition.lang, cct.condition.value)
				condition = new Condition(filter)
			}
			ChildContext context = new ChildContext(cct.context as Integer, cct.index as Integer, condition, propDef);
			path << context

			// new parent
			parentType = propertyType
		}

		new PropertyEntityDefinition(typeDef, path, schemaSpace, typeEntity.filter)
	}
}
