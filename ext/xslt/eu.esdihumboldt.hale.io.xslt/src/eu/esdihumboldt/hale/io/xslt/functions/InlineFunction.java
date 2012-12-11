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

import eu.esdihumboldt.hale.common.align.model.Cell;

/**
 * Direct representation of a transformation function as inline XSLT.
 * 
 * @author Simon Templer
 */
public interface InlineFunction extends XslFunction {

	/**
	 * Get the XSL sequence defining the target of the given mapping cell. This
	 * will be the content of a <code>xsl:attribute</code> or
	 * <code>xsl:element</code> instruction.
	 * 
	 * TODO specify selection path for sources!
	 * 
	 * @param cell the mapping cell
	 * @return the XML fragment to be used as part of the attribute or element
	 *         sequence constructor
	 */
	public String getSequence(Cell cell);

}
