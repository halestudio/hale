/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.internal

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellExplanation
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Entity
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager


/**
 * Creates information on a cell that can be inserted into an XSL document.
 * 
 * @author Simon Templer
 */
public class CellXslInfo {

	public static String getInfo(Cell cell) {
		StringBuilder result = new StringBuilder()
		result << "<!-- Cell ${cell.id} -->"

		// source
		if (cell.source?.values()) {
			cell.source.values().each{
				result << "<!-- Source: -->"
				result << entityInfo(it)
			}
		}

		// target
		if (cell.target?.values()) {
			cell.target.values().each{
				result << "<!-- Target: -->"
				result << entityInfo(it)
			}
		}

		// function
		FunctionDefinition<?> function = FunctionUtil.getFunction(cell.transformationIdentifier, null);
		if (function) {
			result << "<!-- Relation: $function.displayName -->"

			CellExplanation explanation = function.explanation
			if (explanation) {
				String plain = explanation.getExplanation(cell, null)
				if (plain) {
					result << "<!-- $plain -->"
				}
			}
		}

		// notes
		def notes = CellUtil.getNotes(cell)
		if (notes) {
			// replace all double dashes in notes
			notes = notes.replaceAll(/\-\-+/, '-')
			result << "<!-- Notes: -->"
			result << "<!-- $notes -->"
		}

		result.toString()
	}

	private static String entityInfo(Entity entity) {
		StringBuilder result = new StringBuilder()

		EntityDefinition ed = entity.definition

		// type
		result << "<!--   Type: ${ed.type.displayName} -->"
		if (ed.filter) {
			def filter = FilterDefinitionManager.instance.asString(ed.filter)
			result << "<!--   Type condition: $filter -->"
		}

		// children
		if (ed.propertyPath) {
			result << "<!--   Properties: "
			ed.propertyPath.eachWithIndex{ ChildContext element, def pos ->
				if (element.child.asProperty()) {
					if (pos > 0) result << '.'
					result << element.child.displayName
					String contextInfo = contextInfo(element)
					if (contextInfo) {
						result << "[$contextInfo]"
					}
				}
			}
			result << " -->"
		}

		result.toString()
	}

	private static String contextInfo(ChildContext context) {
		if (context.condition?.filter) {
			FilterDefinitionManager.instance.asString(context.condition.filter)
		}
		else if (context.index) {
			context.index
		}
		else {
			null
		}
	}
}
