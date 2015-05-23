/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.custom.internal;

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity
import eu.esdihumboldt.hale.common.align.io.LoadAlignmentContext
import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.util.groovy.xml.NSDOMBuilder
import eu.esdihumboldt.util.groovy.xml.NSDOMCategory

/**
 * Complex value definition for custom property functions.
 * 
 * @author Simon Templer
 */
public class DefaultCustomPropertyFunctionType implements
ComplexValueType<DefaultCustomPropertyFunction, LoadAlignmentContext> {

	public static final String NS_CUSTOM_FUNCTION = 'http://www.esdi-humboldt.eu/hale/custom-function'

	@Override
	public DefaultCustomPropertyFunction fromDOM(Element fragment, LoadAlignmentContext context) {
		Map<Value, Value> values = new HashMap<Value, Value>();

		DefaultCustomPropertyFunction result = new DefaultCustomPropertyFunction()

		use (NSDOMCategory) {
			// attributes
			result.identifier = fragment.getAttribute('identifier')
			result.name = fragment.getAttribute('name')
			result.functionType = fragment.getAttribute('type')

			def sources = []
			// input
			for (source in fragment.children(NS_CUSTOM_FUNCTION, 'input')) {
				sources << entityFromTag(source, context)
			}
			result.sources = sources
			// output
			def targetTag = fragment.firstChild(NS_CUSTOM_FUNCTION, 'output')
			if (targetTag) {
				result.target = entityFromTag(targetTag, context)
			}

			// definition
			def defTag = fragment.firstChild(NS_CUSTOM_FUNCTION, "definition")
			if (defTag) {
				result.functionDefinition = DOMValueUtil.fromTag(defTag, context)
			}
		}

		result
	}

	@Override
	public Element toDOM(DefaultCustomPropertyFunction value) {
		def builder = NSDOMBuilder.newBuilder([cf: NS_CUSTOM_FUNCTION])

		def fragment = builder.'cf:custom-property-function'(
				identifier: value.identifier,
				name: value.name,
				type: value.functionType) {

					// input variables
					for (def source in value.sources) {
						entityTag(builder, 'cf:input', source)
					}

					// output
					entityTag(builder, 'cf:output', value.target)

					DOMValueUtil.valueTag(builder, 'cf:definition', value.functionDefinition ?: Value.NULL)
				}

		return fragment;
	}

	DefaultCustomPropertyFunctionEntity entityFromTag(Element element, LoadAlignmentContext context) {
		DefaultCustomPropertyFunctionEntity entity = new DefaultCustomPropertyFunctionEntity()

		use (NSDOMCategory) {
			// attributes
			entity.name = element.getAttribute('name')
			entity.minOccurrence = Integer.parseInt(element.getAttribute('minOccurs'))
			entity.maxOccurrence = Integer.parseInt(element.getAttribute('maxOccurs'))
			entity.eager = Boolean.parseBoolean(element.getAttribute('eager'))

			// binding class
			def bindingElem = element.firstChild(NS_CUSTOM_FUNCTION, 'binding')
			if (bindingElem) {
				try {
					entity.bindingClass = Class.forName(bindingElem.text())
				} catch (e) {
					//FIXME
					e.printStackTrace()
				}
			}

			// TODO binding type
		}

		entity
	}

	Element entityTag(NSDOMBuilder builder, String tagName, DefaultCustomPropertyFunctionEntity entity) {
		if (entity) {
			builder."$tagName"(
					name: entity.name,
					minOccurs: entity.minOccurrence,
					maxOccurs: entity.maxOccurrence,
					eager: entity.eager) {
						if (entity.bindingClass) {
							// binding class
							'cf:binding'(entity.bindingClass.getName())
						}
						//TODO binding type
					}
		}
		else {
			null
		}
	}

	@Override
	public Class<? extends LoadAlignmentContext> getContextType() {
		return LoadAlignmentContext.class;
	}

}
