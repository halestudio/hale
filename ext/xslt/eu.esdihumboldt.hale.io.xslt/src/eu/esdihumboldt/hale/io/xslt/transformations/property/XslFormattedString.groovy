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

package eu.esdihumboldt.hale.io.xslt.transformations.property;

import java.util.regex.Pattern

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation

/**
 * XSLT representation of the FormattedString function.
 * 
 * @author Simon Templer
 */
class XslFormattedString extends AbstractFunctionTransformation implements FormattedStringFunction {

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
			XsltGenerationContext context, Cell typeCell) {
		// get the pattern parameter
		def pattern = CellUtil.getFirstParameter(cell, PARAMETER_PATTERN).as(String)
		if (!pattern) {
			// empty text if no pattern
			return "<xsl:text />"
		}

		// map pattern variable names to XPath expressions
		def varNames = [:]
		for (XslVariable var in variables.get(ENTITY_VARIABLE)) {
			FormattedStringFunction.addValue(varNames, var.XPath, var.entity)
		}

		// replace markers in pattern
		/*
		 * FIXME this is quick and dirty! doesn't handle escaping
		 * (like in FormattedString) or single quotes occurring in
		 * the pattern
		 */
		for (def entry in varNames.entrySet()) {
			def name = entry.key
			def xpath = entry.value
			pattern = pattern.replaceAll(Pattern.quote("{$name}"), "', $xpath, '");
		}

		//TODO check if all variables are actually there and provide def:null otherwise?

		"""
		<xsl:value-of select="concat('$pattern')" />
		"""
	}
}
