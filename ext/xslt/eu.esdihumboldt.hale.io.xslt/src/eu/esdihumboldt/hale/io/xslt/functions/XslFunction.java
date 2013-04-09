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

package eu.esdihumboldt.hale.io.xslt.functions;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext;

/**
 * Provides the concrete XSL implementation of a property transformation.
 * 
 * @author Simon Templer
 */
public interface XslFunction {

	/**
	 * Get the XSL sequence defining the target of the given mapping cell. This
	 * will be the content of a <code>xsl:attribute</code> or
	 * <code>xsl:element</code> instruction.
	 * 
	 * @param cell the mapping cell
	 * @param variables the function variables, variable names are mapped to the
	 *            XSL variables containing the corresponding XPath expressions,
	 *            but not guaranteed to be in the same order as
	 *            {@link Cell#getSource()}
	 * @param xsltContext the XSLT generation context
	 * @param typeCell the type cell in which context the function is executed
	 * @return the XML fragment to be used as part of the attribute or element
	 *         sequence constructor
	 */
	public String getSequence(Cell cell, ListMultimap<String, XslVariable> variables,
			XsltGenerationContext xsltContext, Cell typeCell);

}
