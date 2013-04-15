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

package eu.esdihumboldt.hale.io.xslt.transformations.property.inline;

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation


/**
 * Inline property type transformation.
 * 
 * @author Simon Templer
 */
public class XslInline extends AbstractFunctionTransformation {

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
	XsltGenerationContext xsltContext, Cell typeCell) {
		EntityDefinition source = CellUtil.getFirstEntity(cell.source)?.definition;
		EntityDefinition target = CellUtil.getFirstEntity(cell.target)?.definition;

		TypeDefinition sourceType = source?.definition.asProperty()?.propertyType
		TypeDefinition targetType = target?.definition.asProperty()?.propertyType

		if (sourceType && targetType) {
			// find appropriate cell in alignment
			Alignment alignment = xsltContext.alignment
			Cell inlineCell = null

			/*
			 * XXX For now only supporting type cells w/o an type filters. 
			 */
			TypeEntityDefinition sted = new TypeEntityDefinition(sourceType, SchemaSpaceID.SOURCE, null);
			TypeEntityDefinition tted = new TypeEntityDefinition(targetType, SchemaSpaceID.TARGET, null);

			def sourceCells = alignment.getCells(sted);
			def targetCells = alignment.getCells(tted);
			def cells = sourceCells.intersect(targetCells)

			if (cells) {
				inlineCell = cells.toList()[0]
			}

			if (inlineCell) {
				def select = variables.get(null)[0].XPath
				return """
					<xsl:for-each select="$select">
						<xsl:call-template name="${xsltContext.getInlineTemplateName(inlineCell)}" />
					</xsl:for-each>
				"""
			}
		}

		'<def:null />'
	}
}
