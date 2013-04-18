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

import com.google.common.collect.ListMultimap

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.CellUtil
import eu.esdihumboldt.hale.common.align.model.ParameterValue
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslVariable
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractFunctionTransformation

/**
 * XSL transformation function based on a XPath expression.
 * 
 * @author Simon Templer
 */
public class GenericXPath extends AbstractFunctionTransformation {

	/**
	 * The name of the parameter specifying the XPath expression.
	 */
	public static final String PARAM_XPATH = 'select'

	/**
	 * The entity name for script variables.
	 */
	public static final String ENTITY_VAR = 'var'

	/**
	 * Prefix for referencing the entities inside the custom XSL script.
	 */
	public static final String PREFIX_VAR = 'var'

	@Override
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
	XsltGenerationContext xsltContext, Cell typeCell) {
		StringBuilder builder = new StringBuilder()
		StringBuilder test = new StringBuilder()

		// define source variables
		int number = 1;
		for (XslVariable var : variables.get(ENTITY_VAR)) {
			builder << """
					<xsl:variable name="var${number}" select="${var.getXPath()}" />
				"""

			// build test expression
			if (number > 1) test << ' and '
			test << "\$var${number}"

			number++
		}

		// add XPath expression
		ParameterValue val = CellUtil.getFirstParameter(cell, PARAM_XPATH)
		if (test) {
			// variables
			builder << """
				<xsl:choose>
					<xsl:when test="${test}">
						<xsl:value-of select="${val.as(String)}" />
					</xsl:when>
					<xsl:otherwise>
						<def:null />
					</xsl:otherwise>
				</xsl:choose>
			"""
		}
		else {
			// no variables
			builder << """<xsl:value-of select="${val.as(String)}" />"""
		}

		return builder.toString()
	}
}
