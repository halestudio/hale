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

package eu.esdihumboldt.hale.ui.service.align.resolver

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
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition
import groovy.transform.TypeChecked


/**
 * Creates dummy entity definitions for {@link AbstractEntityType}s.
 * 
 * @author Simon Templer
 */
@TypeChecked
@SuppressWarnings("restriction")
class EntityToDummyDef {

	static EntityDefinition toDef(AbstractEntityType entity) {
		// delegate to concrete methods
		toDef(entity)
	}

	static TypeEntityDefinition toDef(ClassType clazz) {
		toDef(clazz.type)
	}

	static TypeEntityDefinition toDef(ClassType.Type clazz) {
		TypeDefinition type = new DefaultTypeDefinition(new QName(clazz.ns, clazz.name))

		Filter filter = null
		if (clazz.condition) {
			filter = FilterDefinitionManager.instance.from(clazz.condition.lang, clazz.condition.value)
		}

		new TypeEntityDefinition(type, SchemaSpaceID.SOURCE, filter)
	}

	static EntityDefinition toDef(PropertyType property) {
		TypeEntityDefinition typeEntity = toDef(property.type)

		DefaultTypeDefinition typeDef = (DefaultTypeDefinition) typeEntity.definition
		DefaultTypeDefinition parentType = typeDef

		List<ChildContext> path = []

		property.child.eachWithIndex { ChildContextType cct, int index ->
			// create child for parent type
			DefaultTypeDefinition propertyType = new DefaultTypeDefinition(new QName("UnknownPropertyType_$index"))
			DefaultPropertyDefinition propDef = new DefaultPropertyDefinition(new QName(cct.ns, cct.name), parentType, propertyType)

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

		new PropertyEntityDefinition(typeDef, path, SchemaSpaceID.SOURCE, typeEntity.filter)
	}
}
