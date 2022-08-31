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

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Map.Entry

import org.w3c.dom.Element

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter
import com.vladsch.flexmark.util.data.DataHolder;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellExplanation
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.ParameterValue
import eu.esdihumboldt.hale.common.core.HalePlatform
import eu.esdihumboldt.hale.common.core.io.Value
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo
import eu.esdihumboldt.hale.common.core.service.ServiceProvider
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.Schema
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import eu.esdihumboldt.util.xml.XmlUtil
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Alignment JSON representation helper.
 * 
 * @author Simon Templer
 */
@CompileStatic
class AlignmentJson {

	public static String cellExplanation(Cell cell, CellExplanation explanation, ServiceProvider services,
			Locale locale = Locale.getDefault()) {

		String exp = null
		if (explanation) {
			exp = explanation.getExplanationAsHtml(cell, services, locale)
			if (!exp) {
				exp = explanation.getExplanation(cell, services, locale)
				if (exp) {
					exp = markdownToHtml(exp)
				}
			}
		}

		exp
	}

	private static Object getValueRepresentation(Value value, ValueRepresentation valueRep) {
		if (valueRep != null) {
			return valueRep.getValueRepresentation(value)
		}

		// fall-back to default representation
		if (value.isRepresentedAsDOM()) {
			Element element = value.getDOMRepresentation()
			return element != null ? XmlUtil.serialize(element, false) : null
		}
		else {
			return value.getStringRepresentation()
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	static Set<Locale> alignmentInfoJSON(Alignment alignment, JsonStreamBuilder json,
			ServiceProvider services, ProjectInfo project, CellJsonExtension ext,
			ValueRepresentation valueRep, Locale locale, SchemaSpace sourceSchemas, SchemaSpace targetSchemas) {

		Set<Locale> collectedLocales = new HashSet<>()

		json {
			json.export {
				json.timestamp DateTimeFormatter.ISO_INSTANT.format(Instant.now())
				json.haleVersion HalePlatform.coreVersion as String
			}
			if (project) {
				json.project {
					if (project.name) {
						json.name project.name
					}
					if (project.author) {
						json.author project.author
					}
					if (project.haleVersion) {
						json.haleVersion project.haleVersion as String
					}
					if (project.created) {
						json.created DateTimeFormatter.ISO_INSTANT.format(project.created.toInstant())
					}
					if (project.modified) {
						json.modified DateTimeFormatter.ISO_INSTANT.format(project.modified.toInstant())
					}
					if (project.description) {
						json.description markdownToHtml(project.description)
					}
				}
			}
			if (sourceSchemas) {
				sourceSchemas.schemas.each { Schema schema ->
					'sourceSchemas[]' {
						AlignmentJson.schemaInfoJson(schema, json)
					}
				}
			}
			if (targetSchemas) {
				targetSchemas.schemas.each { Schema schema ->
					'targetSchemas[]' {
						AlignmentJson.schemaInfoJson(schema, json)
					}
				}
			}
			alignment.cells.each { Cell cell ->
				'cells[]' {
					def cellLocales = AlignmentJson.cellInfoJSON(cell, json, services, ext, valueRep, locale)
					if (cellLocales) {
						collectedLocales.addAll(cellLocales)
					}
				}
			}
		}

		collectedLocales
	}

	public static void schemaInfoJson(Schema schema, JsonStreamBuilder json) {
		json {
			json 'namespace', schema.namespace
			if (schema.location) {
				json 'location', schema.location.toString()
			}
		}
	}

	/**
	 * Create a JSON representation from a cell.
	 */
	public static Set<Locale> cellInfoJSON(Cell cell, JsonStreamBuilder json, ServiceProvider services,
			CellJsonExtension ext = null, ValueRepresentation valueRep = null, Locale locale = Locale.getDefault()) {

		// collect locale content is available for
		Set<Locale> collectedLocales = new HashSet<>()

		// collect cell information

		// get the associated function
		FunctionDefinition function = FunctionUtil.getFunction(cell
				.getTransformationIdentifier(), services)

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
					FunctionParameterDefinition paramDef = function?.getParameter(entry.key)
					json 'functionParameters[]', {
						// label and value
						json 'paramLabel', paramDef?.displayName?:entry.key
						json 'paramName', entry.key
						json 'paramValue', getValueRepresentation(entry.value.intern(), valueRep)
						json 'simple', !entry.value.representedAsDOM
					}
				}
			}
			else {
				// fails w/o -> write empty array
				json 'functionParameters', []
			}

			if (cell.source != null && !cell.source.isEmpty()) {
				cell.source.entries().each { Entry<String, ? extends Entity> entry ->
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
				cell.target.entries().each { Entry<String, ? extends Entity> entry ->
					json 'targets[]', {
						entityJSON(json, entry.key, entry.value, ext)
					}
				}
			}
			else {
				// fails w/o -> write empty array
				json 'targets', []
			}


			if (function?.explanation) {
				def explLocales = function.explanation.getSupportedLocales()
				if (explLocales) {
					// explanations by locale
					json 'explanations', {
						explLocales.each { Locale explLocale ->
							collectedLocales.add(explLocale) // add locale
							json explLocale as String, cellExplanation(cell, function.explanation, services, explLocale)
						}
					}
				}
				else {
					// simple single explanation
					String explanation = cellExplanation(cell, function.explanation, services, locale)
					json 'explanation', explanation
				}
			}

			if (ext != null) {
				ext.augmentCellJson(cell, json)
			}
		}

		collectedLocales
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

	/**
	 * Convert markdown (pegdown) to HTML.
	 *
	 * @param text the markdown
	 * @return the processed markdown as HTML
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	public static String markdownToHtml(String text) {
		DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(true,
				Extensions.AUTOLINKS |
				Extensions.SUPPRESS_ALL_HTML |
				Extensions.HARDWRAPS |
				Extensions.SMARTYPANTS |
				Extensions.TABLES |
				Extensions.FENCED_CODE_BLOCKS |
				Extensions.STRIKETHROUGH |
				Extensions.DEFINITIONS)

		Parser PARSER = Parser.builder(OPTIONS).build()
		HtmlRenderer RENDERER = HtmlRenderer.builder(OPTIONS).build()

		return RENDERER.render(PARSER.parse(text))
	}
}
