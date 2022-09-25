/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.io.impl.internal

import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType.Type
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ConditionType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import eu.esdihumboldt.hale.common.instance.model.Filter
import jakarta.xml.bind.JAXBElement



/**
 * Static methods for conversion from {@link EntityDefinition}s to JAXB.
 *
 * @author Kai Schwierczek
 */
class EntityDefinitionToJaxb {

	private EntityDefinitionToJaxb() {
	}

	public static JAXBElement<ClassType> convert(TypeEntityDefinition type) {
		ObjectFactory of = new ObjectFactory()
		JAXBElement<ClassType> element = of.createClass(new ClassType())
		fill(element, type)
		return element
	}

	public static JAXBElement<PropertyType> convert(PropertyEntityDefinition property) {
		ObjectFactory of = new ObjectFactory()
		JAXBElement<PropertyType> element = of.createProperty(new PropertyType())
		fill(element, property)
		return element
	}

	private static fill(JAXBElement<? extends ClassType> element, EntityDefinition entity) {
		// set the type
		element.value.type = new Type(
				name: entity.type.name.localPart,
				ns: entity.type.name.namespaceURI ?: null)
		element.value.type.condition = entity.filter ?
				convert(entity.filter) : null

		// add children
		if (entity instanceof PropertyEntityDefinition) {
			for (ChildContext child in entity.propertyPath) {
				element.value.child << convert(child)
			}
		}
	}

	protected static ChildContextType convert(ChildContext context) {
		ChildContextType result = new ChildContextType()

		result.name = context.child.name.localPart
		result.ns = context.child.name.namespaceURI ?: null

		result.context = context.contextName
		result.index = context.index
		result.condition = context.condition ? convert(context.condition.filter) : null

		return result
	}

	protected static ConditionType convert(Filter filter) {
		if (!filter) return null

		def rep = FilterDefinitionManager.getInstance().asPair(filter)

		new ConditionType(lang: rep.first, value: rep.second)
	}
}
