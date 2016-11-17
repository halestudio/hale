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

import com.google.common.collect.ListMultimap
import com.google.common.collect.MultimapBuilder

import eu.esdihumboldt.hale.common.align.extension.annotation.AnnotationExtension
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AbstractParameterType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AnnotationType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CellType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ComplexParameterType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.CustomFunctionType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.DocumentationType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.NamedEntityType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ParameterType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PriorityType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.TransformationModeType
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.AlignmentType.Base
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType.DisableFor
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ModifierType.Transformation
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.BaseAlignmentCell
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.ParameterValue
import eu.esdihumboldt.hale.common.align.model.Property
import eu.esdihumboldt.hale.common.align.model.TransformationMode
import eu.esdihumboldt.hale.common.align.model.Type
import eu.esdihumboldt.hale.common.core.io.HaleIO
import eu.esdihumboldt.hale.common.core.io.PathUpdate
import eu.esdihumboldt.hale.common.core.io.report.IOReporter
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode




/**
 * Converts an {@link Alignment} to a {@link AlignmentType} for serialization with JAXB.
 * 
 * @author Simon Templer
 */
@CompileStatic
class AlignmentToJaxb {

	private final Alignment alignment
	private final IOReporter reporter
	private final ObjectFactory of = new ObjectFactory()
	private final PathUpdate pathUpdate

	/**
	 * Create a new converter.
	 * 
	 * @param alignment the alignment to convert
	 * @param reporter the reporter to use for reporting problems, may be <code>null</code>
	 * @param pathUpdate to update relative paths in case of a path change
	 */
	AlignmentToJaxb(Alignment alignment, IOReporter reporter, PathUpdate pathUpdate) {
		this.alignment = alignment
		this.reporter = reporter
		this.pathUpdate = pathUpdate
	}

	/**
	 * @return
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	AlignmentType convert() throws Exception {
		AlignmentType align = new AlignmentType()

		alignment.baseAlignments.sort().collect(align.base) {
			new Base(prefix: it.key, location: pathUpdate.findLocation(it.value, true, false, true))
		}
		
		alignment.customPropertyFunctions.sort().collect(align.customFunction) {
			def cft = new CustomFunctionType()
			
			def element = HaleIO.getComplexElement(it.value)
			cft.setAny(element)
			
			cft
		}

		// convert cells
		for (Cell cell in alignment.cells) {
			if (!(cell instanceof BaseAlignmentCell)) {
				align.cellOrModifier << convert(cell)
			}
			addModifier(cell, align);
		}
		
		// sort cells
		align.cellOrModifier.sort(CellOrModifierComparator.instance)

		return align
	}

	protected void addModifier(Cell cell, AlignmentType align) {
		Set<String> disabledFor = cell.disabledFor
		TransformationMode mode = cell.transformationMode
		if (cell instanceof BaseAlignmentCell) {
			disabledFor = ((BaseAlignmentCell) cell).additionalDisabledFor
			if (!cell.overridesTransformationMode()) {
				// only store if the original value actually is overridden
				mode = null
			}
		}
		else {
			// only store mode if it is not the default
			if (mode == Cell.DEFAULT_TRANSFORMATION_MODE) {
				mode = null
			}
		}
		
		if (mode || disabledFor) {
			ModifierType modifier = new ModifierType()
			modifier.cell = cell.id
			if (disabledFor) {
				disabledFor.collect(modifier.disableFor) { String it ->
					new DisableFor(parent: it)
				}
			}
			if (mode) {
				String name = mode.name()
				TransformationModeType tmt = TransformationModeType.fromValue(name)
				modifier.transformation = new Transformation(mode: tmt)
			}
			align.cellOrModifier << modifier;
		}
	}

	protected CellType convert(Cell cell) {
		CellType result = new CellType()

		// the transformation id
		result.relation = cell.transformationIdentifier

		// the cell id
		result.id = cell.id;

		// the cell priority
		def priorityType = PriorityType.fromValue(cell.priority.value());
		result.priority = priorityType;

		// the transformation parameters
		if (cell.transformationParameters) {
			ListMultimap<String, ParameterValue> params = MultimapBuilder.treeKeys().arrayListValues().build(cell.transformationParameters)
			params.entries()?.each { Entry<String, ParameterValue> param ->
				def p = convert(param.key, param.value)
				if (p) result.abstractParameter << p
			}
		}

		// source entities
		if (cell.source) {
			ListMultimap<String, ? extends Entity> sources = (ListMultimap<String, ? extends Entity>) MultimapBuilder.treeKeys().arrayListValues().build(cell.source)
			sources.entries()?.each { Entry<String, ? extends Entity> entity ->
				result.source << convert(entity.key, entity.value)
			}
		}

		// target entities
		if (cell.target) {
			ListMultimap<String, ? extends Entity> targets = (ListMultimap<String, ? extends Entity>) MultimapBuilder.treeKeys().arrayListValues().build(cell.target)
			targets.entries()?.each { Entry<String, ? extends Entity> entity ->
				result.target << convert(entity.key, entity.value)
			}
		}

		// documentations
		if (cell.documentation) {
			ListMultimap<String, String> docs = MultimapBuilder.treeKeys().arrayListValues().build(cell.documentation)
			docs.entries().each { Entry<String, String> it ->
				// create documentation element from each multimap entry
				result.documentationOrAnnotation << new DocumentationType(type: it.key, value: it.value)
			}
		}

		// annotations
		for (String type in cell.annotationTypes.sort()) {
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
		if (!value.representedAsDOM) {
			// normal value
			return of.createParameter(new ParameterType(name: name, value: value.stringRepresentation,
			type: (value.needsProcessing() ? value.type : null)))
		}
		else {
			// complex value or element
			return of.createComplexParameter(
			new ComplexParameterType(name: name, any: value.getDOMRepresentation()))
		}
	}

	protected NamedEntityType convert(String name, Entity entity) {
		NamedEntityType result = new NamedEntityType()

		// the entity name
		result.name = name

		// create the entity object
		switch (entity) {
			case Type:
			result.abstractEntity = EntityDefinitionToJaxb.convert(((Type)entity).definition);
			break
			case Property:
			result.abstractEntity = EntityDefinitionToJaxb.convert(((Property)entity).definition);
			break
			default:
			throw new IllegalArgumentException("Illegal entity ${entity.class}")
		}

		return result
	}

}
