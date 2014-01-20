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

import org.pegdown.Extensions
import org.pegdown.PegDownProcessor

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import groovy.json.JsonBuilder
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

		// for each cell create the JSON representation
		def cellData = [:]
		alignment.cells.each { Cell cell ->
			// create JSON representation
			cellData[cell.id] = cellInfoJSON(cell)
		}
		b.cells = cellData

		b
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private static String cellInfoJSON(Cell cell) {
		// collect cell information

		// get the associated function
		AbstractFunction<?> function = FunctionUtil.getFunction(cell
				.getTransformationIdentifier())

		// create JSON representation
		JsonBuilder builder = new JsonBuilder()
		builder.call {
			// function name
			functionName function.displayName

			// list of function parameters
			if (cell.transformationParameters) {
				functionParameters(cell.transformationParameters.entries().collect {
					// label and value
					[paramLabel: it.key, paramValue: it.value as String]
				})
			}
			else {
				functionParameters([])
			}

			if (cell.source) {
				sources(entitiesJSON(builder, cell.source))
			}
			else {
				sources([])
			}
			if (cell.target) {
				targets(entitiesJSON(builder, cell.target))
			}
			else {
				targets([])
			}
		}
		builder.toString()
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private static def entitiesJSON(JsonBuilder builder, ListMultimap<String, Entity> entities) {
		entities.entries().collect {
			Entity entity = it.value
			builder.call {
				EntityDefinition ede = entity.definition
				// property path
				propertyPath(
						// type (TODO include filter)
						[typeString(ede)]+
						// properties
						ede.propertyPath.collect { ChildContext context ->
							childString(context)
						}
						)

				// property descriptions
				if (false) //XXX disabled
					propertyDescriptions(
							// type
							[
								builder.call {
									if (ede.type.description) {
										typeDescription markdownToHtml(ede.type.description)
									}
								}]
							+
							// children
							ede.propertyPath.collect { ChildContext context ->
								builder.call {
									if (context.child.asProperty()) {
										// property type
										propertyType context.child.asProperty().propertyType.displayName
									}
									// cardinality
									Cardinality card = DefinitionUtil.getCardinality(context.child)
									propertyCardinality card as String
									// description
									if (context.child.description) {
										propertyDescription markdownToHtml(context.child.description)
									}
								}
							}
							)
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
