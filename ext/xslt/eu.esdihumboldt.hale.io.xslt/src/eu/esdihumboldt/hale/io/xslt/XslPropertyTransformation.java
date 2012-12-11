/*
 * Copyright (c) 2012 Fraunhofer IGD
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

package eu.esdihumboldt.hale.io.xslt;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.io.xslt.functions.XslFunction;

/**
 * Translates a property transformation function to a XSLT template. Property
 * transformations designed to live through the whole XSLT generation process so
 * state can be shared between different calls to {@link #selectFunction(Cell)}.
 * 
 * @author Simon Templer
 */
public interface XslPropertyTransformation extends XslTransformation {

	/**
	 * Select the function that should handle the given property cell.
	 * 
	 * @param cell a cell representing a property transformation with a
	 *            transformation function associated to this XSL transformation
	 * @return the selected function
	 * @throws TransformationException if for the given cell no function can be
	 *             supplied
	 */
	public XslFunction selectFunction(Cell cell) throws TransformationException;

}
