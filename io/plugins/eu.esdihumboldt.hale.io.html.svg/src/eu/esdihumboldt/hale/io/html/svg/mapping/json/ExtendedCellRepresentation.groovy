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

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil
import eu.esdihumboldt.hale.common.align.io.impl.internal.EntityDefinitionToJaxb
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.core.service.ServiceProvider
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import groovy.transform.CompileStatic


/**
 * Extended cell JSON representation.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
@CompileStatic
class ExtendedCellRepresentation implements CellJsonExtension {

	private final Multimap<String, String> parentIds = HashMultimap.create()

	private final ServiceProvider serviceProvider

	private final JaxbAlignmentJson jaxbJson = new JaxbAlignmentJson()

	ExtendedCellRepresentation(Alignment alignment, ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider

		// collect parent cells of property cells
		def typeCells = (Collection<Cell>) alignment.getTypeCells() // Groovy CompileStatic can't deal properly with ? extends ...
		typeCells.each { Cell typeCell ->
			def propertyCells = (Collection<Cell>) alignment.getPropertyCells(typeCell, true, false) // Groovy CompileStatic can't deal properly with ? extends ...
			propertyCells.each { Cell propertyCell ->
				parentIds.put(propertyCell.getId(), typeCell.getId())
			}
		}
	}

	@Override
	public void augmentCellJson(Cell cell, JsonStreamBuilder json) {
		// cell identifier
		json 'cellId', cell.getId()

		// parents
		parentIds.get(cell.getId()).each { String parentId ->
			json 'parentCells', true, parentId
		}

		// disabled
		cell.getDisabledFor().each { String cellId ->
			json 'disabledFor', true, cellId
		}

		// transformation mode
		if (AlignmentUtil.isTypeCell(cell)) {
			json 'mode', cell.getTransformationMode().name()
		}

		FunctionDefinition<?> function = FunctionUtil.getFunction(cell.transformationIdentifier, serviceProvider)

		// state if any parameters are supported / defined
		json 'configurable', (function?.definedParameters) as boolean
	}

	@Override
	public void augmentEntityJson(Entity entity, String name,
			JsonStreamBuilder json) {
		/*
		 * Information provided for an entity is not sufficient and not in the
		 * correct form to be able to identify an entity. Thus the entity
		 * representation is provided in addition.
		 */

		EntityDefinition entityDef = entity.definition
		ClassType xentity
		if (entityDef instanceof PropertyEntityDefinition) {
			xentity = EntityDefinitionToJaxb.convert(entityDef).value
		}
		else if (entityDef instanceof TypeEntityDefinition) {
			xentity = EntityDefinitionToJaxb.convert(entityDef).value
		}

		json 'entity', {
			jaxbJson.writeEntity(json, xentity)
		}
	}

}
