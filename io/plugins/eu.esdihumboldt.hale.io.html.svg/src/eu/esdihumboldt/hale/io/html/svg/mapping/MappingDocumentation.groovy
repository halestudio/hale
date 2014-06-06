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

package eu.esdihumboldt.hale.io.html.svg.mapping

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml as escape

import java.util.Map.Entry

import org.pegdown.Extensions
import org.pegdown.PegDownProcessor
import org.w3c.dom.Element

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.ParameterValue
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.util.Identifiers
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import eu.esdihumboldt.util.xml.XmlUtil
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * Prepares alignment and project information for use with the mapping documentation template.
 * 
 * @author Simon Templer
 */
@CompileStatic
class MappingDocumentation {

	/**
	 * Create a binding for use with the mapping documentation template.
	 * 
	 * @param projectInfo the project information
	 * @param alignment the project alignment
	 * @return the template bindings
	 */
	static Map createBinding(ProjectInfo projectInfo, Alignment alignment) {
		def b = [:]

		// make project info available
		b.project = projectInfoBinding(projectInfo)

		// make cell information available
		b.alignment = alignmentBinding(alignment)

		b
	}

	private static Map alignmentBinding(Alignment alignment) {
		def b = [:]

		// cell ID mapping (as the original cell IDs may contain invalid characters)
		Identifiers<Cell> simpleIds = new Identifiers<Cell>(Cell.class, false)

		// for each type cell
		b.typeCells = alignment.typeCells.collect { Cell typeCell ->
			[ //
				// type cell ID
				id: simpleIds.getId(typeCell), //

				// display name
				//TODO

				// IDs of cells associated to the type cell
				cells: alignment.getPropertyCells(typeCell).collect { //
					Cell propertyCell ->
					simpleIds.getId(propertyCell)
				}]
		}

		// for each cell create the JSON representation
		def cellData = [:]
		def cellExplanations = [:]
		alignment.cells.each { Cell cell ->
			def id = simpleIds.getId(cell)
			// create JSON representation
			StringWriter writer = new StringWriter()
			writer.withWriter { Writer w ->
				cellInfoJSON(cell, new JsonStreamBuilder(w))
			}
			cellData[id] = writer.toString()

			// create cell explanation
			cellExplanations[id] = cellExplanation(cell)
		}
		b.cells = cellData
		b.explanations = cellExplanations

		b
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private static String cellExplanation(Cell cell) {
		// get the associated function
		AbstractFunction<?> function = FunctionUtil.getFunction(cell
				.getTransformationIdentifier())

		String exp = null
		if (function?.explanation) {
			exp = function.explanation.getExplanationAsHtml(cell, null)
			if (!exp) {
				exp = function.explanation.getExplanation(cell, null)
				if (exp) {
					exp = markdownToHtml(exp)
				}
			}
		}

		exp
	}

	private static String getValueRepresentation(Value value) {
		if (value.isRepresentedAsDOM()) {
			Element element = value.getDOMRepresentation()
			return element != null ? XmlUtil.serialize(element, false) : null
		}
		else {
			return value.getStringRepresentation()
		}
	}

	/**
	 * Create a JSON representation from a cell.
	 */
	public static String cellInfoJSON(Cell cell, JsonStreamBuilder json, CellJsonExtension ext = null) {
		// collect cell information

		// get the associated function
		AbstractFunction<?> function = FunctionUtil.getFunction(cell
				.getTransformationIdentifier())

		// create JSON representation of individual cell
		json {
			// function name
			json 'functionName', function?.displayName?:cell.transformationIdentifier

			// function ID
			json 'functionId', cell.transformationIdentifier

			// if the cell is a type relation
			json 'typeRelation', AlignmentUtil.isTypeCell(cell)

			// list of function parameters
			if (cell.transformationParameters) {
				cell.transformationParameters.entries().each { Entry<String, ParameterValue> entry ->
					json 'functionParameters[]', {
						// label and value
						json 'paramLabel', entry.key
						json 'paramValue', getValueRepresentation(entry.value.intern())
						json 'xmlParam', entry.value.representedAsDOM
					}
				}
			}
			else {
				// fails w/o -> write empty array
				json 'functionParameters', []
			}

			if (cell.source != null && !cell.source.isEmpty()) {
				cell.source.entries().each { Entry<String, Entity> entry ->
					json 'sources[]', {
						entityJSON(json, entry.key, entry.value, ext)
					}
				}
			}
			else {
				// fails w/o -> write empty array
				json 'sources', []
			}
			if (cell.target != null && !cell.target.isEmpty()) {
				cell.target.entries().each { Entry<String, Entity> entry ->
					json 'targets[]', {
						entityJSON(json, entry.key, entry.value, ext)
					}
				}
			}
			else {
				// fails w/o -> write empty array
				json 'targets', []
			}

			String explanation = cellExplanation(cell)
			if (explanation) {
				json 'explanation', explanation
			}

			if (ext != null) {
				ext.augmentCellJson(cell, json)
			}
		}
	}

	private static def entityJSON(JsonStreamBuilder json, String name, Entity entity, CellJsonExtension ext) {
		json {
			EntityDefinition ede = entity.definition

			// property path

			// type (TODO include filter)
			json 'propertyPath[]', typeString(ede)
			// children
			ede.propertyPath.each { ChildContext context ->
				json 'propertyPath[]', childString(context)
			}

			// property descriptions

			// type
			json 'propertyDescriptions[]', {
				if (ede.type.description) {
					json 'typeDescription', markdownToHtml(ede.type.description)
				}
			}
			// children
			ede.propertyPath.each { ChildContext context ->
				json 'propertyDescriptions[]', {
					if (context.child.asProperty()) {
						// property type
						json 'propertyType', context.child.asProperty().propertyType.displayName
					}
					// cardinality
					Cardinality card = DefinitionUtil.getCardinality(context.child)
					json 'propertyCardinality', card as String
					// description
					if (context.child.description) {
						json 'propertyDescription', markdownToHtml(context.child.description)
					}
				}
			}

			// short name
			json 'shortName', ede.definition.displayName

			if (ext != null) {
				ext.augmentEntityJson(entity, name, json)
			}
		}
	}

	/**
	 * Creates a string representation of the base type in an entity definition.
	 * 
	 * @param ede the entity definition
	 * @return the type string representation including a potential filter
	 */
	private static String typeString(EntityDefinition ede) {
		if (ede.filter) {
			ede.type.displayName + ' (' + FilterDefinitionManager.getInstance().asString(ede.filter) + ')'
		}
		else {
			ede.type.displayName
		}
	}

	/**
	 * Create a string representation of a specific child context in an entity definition.
	 * 
	 * @param context the child context
	 * @return the child string representation including context information
	 */
	private static String childString(ChildContext context) {
		StringBuilder result = new StringBuilder(context.child.displayName)

		if (context.condition && context.condition.filter) {
			result << ' ('
			result << FilterDefinitionManager.getInstance().asString(context.condition.filter)
			result << ')'
		}
		else if (context.index) {
			result << "[${context.index}]"
		}
		else if (context.contextName) {
			return '(' + result << ')'
		}

		result
	}

	private static Map projectInfoBinding(ProjectInfo project) {
		if (project?.name) {
			def b = [:]

			// project name
			b.name = escape(project.name)

			// project author
			b.author = escape(project.author)

			// project description
			if (project.description) {
				// convert markdown to HTML
				b.description = markdownToHtml(project.description)
			}

			b
		}
		else {
			// don't include project information
			null
		}
	}

	/**
	 * Convert markdown (pegdown) to HTML. 
	 * 
	 * @param text the markdown
	 * @return the processed markdown as HTML
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	private static String markdownToHtml(String text) {
		new PegDownProcessor(
				Extensions.AUTOLINKS |
				Extensions.SUPPRESS_ALL_HTML |
				Extensions.HARDWRAPS |
				Extensions.SMARTYPANTS).
				markdownToHtml(text);
	}
}
