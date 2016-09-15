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

import javax.xml.namespace.QName

import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.custom.DefaultCustomFunctionExplanation
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunction
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionEntity
import eu.esdihumboldt.hale.common.align.custom.DefaultCustomPropertyFunctionParameter
import eu.esdihumboldt.hale.common.align.io.LoadAlignmentContext
import eu.esdihumboldt.hale.common.core.io.ComplexValueType
import eu.esdihumboldt.hale.common.core.io.DOMValueUtil
import eu.esdihumboldt.hale.common.core.io.Text
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
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
				sources << entityFromTag(source, context?.sourceTypes)
			}
			result.sources = sources
			// output
			def targetTag = fragment.firstChild(NS_CUSTOM_FUNCTION, 'output')
			if (targetTag) {
				result.target = entityFromTag(targetTag, context?.targetTypes)
			}

			def params = []
			// parameters
			for (param in fragment.children(NS_CUSTOM_FUNCTION, 'param')) {
				params << paramFromTag(param)
			}
			result.parameters = params

			// definition
			def defTag = fragment.firstChild(NS_CUSTOM_FUNCTION, "definition")
			if (defTag) {
				result.functionDefinition = DOMValueUtil.fromTag(defTag, context)
			}

			// explanation
			def explTag = fragment.firstChild(NS_CUSTOM_FUNCTION, "explanation")
			if (explTag) {
				result.explanation = explanationFromTag(explTag)
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
					if (value.sources) {
						for (def source in value.sources) {
							entityTag(builder, 'cf:input', source)
						}
					}

					// output
					entityTag(builder, 'cf:output', value.target)

					// parameters
					if (value.parameters) {
						for (def param in value.parameters) {
							paramTag(builder, 'cf:param', param)
						}
					}

					// definition
					DOMValueUtil.valueTag(builder, 'cf:definition', value.functionDefinition ?: Value.NULL)

					// explanation
					if (value.explanation) {
						explanationTag(builder, 'cf:explanation', value.explanation)
					}
				}

		return fragment;
	}

	DefaultCustomPropertyFunctionEntity entityFromTag(Element element, TypeIndex types) {
		DefaultCustomPropertyFunctionEntity entity = new DefaultCustomPropertyFunctionEntity()

		use (NSDOMCategory) {
			// attributes
			entity.name = element.getAttributeOrNull('name')
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

			//FIXME binding type
			//XXX for now use qname
			def typeElem = element.firstChild(NS_CUSTOM_FUNCTION, 'type')
			if (typeElem) {
				def name = new QName(typeElem.getAttribute('ns'), typeElem.getAttribute('name'))
				if (types) {
					entity.bindingType = types.getType(name)
				}
				else {
					println('No type index for resolving custom function entity type provided')
				}
			}
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
						//FIXME binding type
						//XXX for now use the type name
						if (entity.bindingType) {
							'cf:type'(name: entity.bindingType.name.localPart, ns: entity.bindingType.name.namespaceURI)
						}
					}
		}
		else {
			null
		}
	}

	DefaultCustomPropertyFunctionParameter paramFromTag(Element element) {
		DefaultCustomPropertyFunctionParameter param = new DefaultCustomPropertyFunctionParameter()

		use (NSDOMCategory) {
			// attributes
			param.name = element.getAttribute('name')
			param.minOccurrence = Integer.parseInt(element.getAttribute('minOccurs'))
			param.maxOccurrence = Integer.parseInt(element.getAttribute('maxOccurs'))

			// binding class
			def bindingElem = element.firstChild(NS_CUSTOM_FUNCTION, 'binding')
			if (bindingElem) {
				try {
					param.bindingClass = Class.forName(bindingElem.text())
				} catch (e) {
					//FIXME
					e.printStackTrace()
				}
			}

			// enumeration
			def values = new LinkedHashSet<String>()
			element.children(NS_CUSTOM_FUNCTION, 'value').collect(values) { it.text() }
			if (values) {
				param.enumeration = values
			}

			// default value
			def defaultElem = element.firstChild(NS_CUSTOM_FUNCTION, 'default')
			if (defaultElem) {
				param.defaultValue = DOMValueUtil.fromTag(defaultElem)
			}

			// parameter description
			def descElem = element.firstChild(NS_CUSTOM_FUNCTION, 'description')
			if (descElem) {
				Value descValue = DOMValueUtil.fromTag(descElem)
				param.description = descValue as String
			}

			// display name
			def displayElem = element.firstChild(NS_CUSTOM_FUNCTION, 'display')
			if (displayElem) {
				Value descValue = DOMValueUtil.fromTag(displayElem)
				param.displayName = descValue as String
			}
		}

		param
	}

	Element paramTag(NSDOMBuilder builder, String tagName, DefaultCustomPropertyFunctionParameter param) {
		if (param) {
			builder."$tagName"(
					name: param.name,
					minOccurs: param.minOccurrence,
					maxOccurs: param.maxOccurrence) {
						if (param.description) {
							// parameter description
							Value descValue = Value.complex(new Text(param.description))
							DOMValueUtil.valueTag(builder, 'cf:description', descValue)
						}
						if (param.displayName) {
							// display name
							DOMValueUtil.valueTag(builder, 'cf:display', Value.of(param.displayName))
						}
						if (param.bindingClass) {
							// binding class
							'cf:binding'(param.bindingClass.getName())
						}
						if (param.enumeration) {
							// enumeration
							for (def value in param.enumeration) {
								'cf:value'(value)
							}
						}
						if (param.defaultValue) {
							// default value
							DOMValueUtil.valueTag(builder, 'cf:default', param.defaultValue)
						}
					}
		}
		else {
			null
		}
	}

	DefaultCustomFunctionExplanation explanationFromTag(Element element) {
		Map<Locale, Value> templates = [:]

		use (NSDOMCategory) {
			element.children(NS_CUSTOM_FUNCTION, 'locale').each { Element localeElem ->
				def templateElem = localeElem.firstChild(NS_CUSTOM_FUNCTION, 'template')
				if (templateElem) {
					Value value = DOMValueUtil.fromTag(templateElem)

					if (value) {
						String language = localeElem.getAttribute('language')
						String country = localeElem.getAttribute('country')
						String variant = localeElem.getAttribute('variant')
						Locale locale = new Locale(language, country, variant)

						templates[locale] = value
					}
				}
			}
		}

		DefaultCustomFunctionExplanation explanation = new DefaultCustomFunctionExplanation(templates, null)
	}

	Element explanationTag(NSDOMBuilder builder, String tagName, DefaultCustomFunctionExplanation explanation) {
		if (explanation) {
			builder."$tagName"(/* TODO add a type? e.g. type: 'gstring-markdown'*/) {
				explanation.templates.each { Locale locale, Value value ->
					builder.'cf:locale'(language: locale.language, country: locale.country, variant: locale.variant) {
						DOMValueUtil.valueTag(builder, 'cf:template', value)
					}
				}
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
