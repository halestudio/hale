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

import java.util.Map.Entry

import javax.xml.bind.JAXBElement

import eu.esdihumboldt.hale.common.align.extension.annotation.AnnotationExtension
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractParameterType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AnnotationType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CellType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ChildContextType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ComplexParameterType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ConditionType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.DocumentationType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ParameterType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType.Type
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.ParameterValue
import eu.esdihumboldt.hale.common.align.model.Property
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.report.IOReporter
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import eu.esdihumboldt.hale.common.instance.model.Filter



/**
 * Converts an {@link Alignment} to a {@link AlignmentType} for serialization with JAXB.
 * 
 * @author Simon Templer
 */
class AlignmentToJaxb {

	private final Alignment alignment
	private final IOReporter reporter
	private final ObjectFactory of = new ObjectFactory()

	/**
	 * Create a new converter.
	 * 
	 * @param alignment the alignment to convert
	 * @param reporter the reporter to use for reporting problems, may be <code>null</code>
	 */
	AlignmentToJaxb(Alignment alignment, IOReporter reporter) {
		this.alignment = alignment
		this.reporter = reporter
	}

	/**
	 * @return
	 */
	AlignmentType convert() throws Exception {
		AlignmentType align = new AlignmentType()

		// convert cells
		for (Cell cell in alignment.cells) {
			align.cell << convert(cell)
		}

		return align
	}

	protected CellType convert(Cell cell) {
		CellType result = new CellType()

		// the transformation id
		result.relation = cell.transformationIdentifier

		// the transformation parameters
		cell.transformationParameters?.entries()?.each { Entry<String, ParameterValue> param ->
			def p = convert(param.key, param.value)
			if (p) result.abstractParameter << p 
		}

		// source entities
		cell.source?.entries()?.each { Entry<String, ? extends Entity> entity ->
			result.source << convert(entity.key, entity.value)
		}

		// target entities
		cell.target?.entries()?.each { Entry<String, ? extends Entity> entity ->
			result.target << convert(entity.key, entity.value)
		}

		// documentations
		cell.documentation.entries().each {
			// create documentation element from each multimap entry
			result.documentationOrAnnotation << new DocumentationType(type: it.key, value: it.value)
		}

		// annotations
		for (String type in cell.annotationTypes) {
			def descriptor = AnnotationExtension.instance.get(type)
			if (descriptor) {
				for (Object value : cell.getAnnotations(type)) {
					result.documentationOrAnnotation << new AnnotationType(type: type, any: descriptor.toDOM(value))
				}
			}
			else {
				//TODO report
			}
		}

		return result
	}

	protected JAXBElement<? extends AbstractParameterType> convert(String name, ParameterValue value) {
		/*
		 * XXX are null parameters working like this OK? or should there be no
		 * parameter created at all? 
		 */
		if (value.value instanceof String || value.value == null) {
			// normal value
			return of.createParameter(new ParameterType(name: name, value: value.value, 
				type: (!value.type || value.type == ParameterValue.DEFAULT_TYPE ? null : value.type)))
		}
		else {
			// complex value or element
			return of.createComplexParameter(
				new ComplexParameterType(name: name, any: HaleIO.getComplexElement(value.value)))
		}
	}

	protected NamedEntityType convert(String name, Entity entity) {
		NamedEntityType result = new NamedEntityType()

		// the entity name
		result.name = name

		// create the entity object
		switch (entity) {
			case eu.esdihumboldt.hale.common.align.model.Type:
			result.abstractEntity = of.createClass(new ClassType())
			break
			case Property:
			result.abstractEntity = of.createProperty(new PropertyType())
			break
			default:
			throw new IllegalArgumentException("Illegal entity ${entity.class}")
		}

		// set the type
		result.abstractEntity.value.type = new Type(
		name: entity.definition.type.name.localPart,
		ns: entity.definition.type.name.namespaceURI ?: null)
		result.abstractEntity.value.type.condition = entity.definition.filter ?
		convert(entity.definition.filter) : null

		// add children
		if (entity instanceof Property) {
			for (ChildContext child in entity.definition.propertyPath) {
				result.abstractEntity.value.child << convert(child)
			}
		}

		return result
	}

	protected ChildContextType convert(ChildContext context) {
		ChildContextType result = new ChildContextType()

		result.name = context.child.name.localPart
		result.ns = context.child.name.namespaceURI ?: null

		result.context = context.contextName
		result.index = context.index
		result.condition = context.condition ? convert(context.condition.filter) : null

		return result
	}

	protected ConditionType convert(Filter filter) {
		if (!filter) return null

		def rep = FilterDefinitionManager.getInstance().asPair(filter)

		new ConditionType(lang: rep.first, value: rep.second)
	}

}
