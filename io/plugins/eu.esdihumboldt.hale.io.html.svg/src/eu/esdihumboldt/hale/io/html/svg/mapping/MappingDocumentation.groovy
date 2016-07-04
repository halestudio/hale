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
import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo
import eu.esdihumboldt.hale.common.core.service.ServiceProvider
import eu.esdihumboldt.hale.io.html.svg.mapping.json.AlignmentJson
import eu.esdihumboldt.util.Identifiers
import eu.esdihumboldt.util.groovy.json.JsonStreamBuilder
import groovy.transform.CompileStatic


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
	static Map createBinding(ProjectInfo projectInfo, Alignment alignment, ServiceProvider services) {
		def b = [:]

		// make project info available
		b.project = projectInfoBinding(projectInfo)

		// make cell information available
		b.alignment = alignmentBinding(alignment, services)

		b
	}

	private static Map alignmentBinding(Alignment alignment, ServiceProvider services) {
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
		Collection<Cell> alignmentCells = (Collection<Cell>) alignment.cells // Groovy CompileStatic can't deal properly with ? extends ...
		alignmentCells.each { Cell cell ->
			def id = simpleIds.getId(cell)
			// create JSON representation
			StringWriter writer = new StringWriter()
			writer.withWriter { Writer w ->
				AlignmentJson.cellInfoJSON(cell, new JsonStreamBuilder(w), services)
			}
			cellData[id] = writer.toString()

			// create cell explanation
			cellExplanations[id] = AlignmentJson.cellExplanation(cell, services)
		}
		b.cells = cellData
		b.explanations = cellExplanations

		b
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
				b.description = AlignmentJson.markdownToHtml(project.description)
			}

			b
		}
		else {
			// don't include project information
			null
		}
	}

}
