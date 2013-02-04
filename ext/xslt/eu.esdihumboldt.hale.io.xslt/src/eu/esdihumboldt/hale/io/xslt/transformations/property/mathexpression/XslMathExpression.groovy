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

package eu.esdihumboldt.hale.io.xslt.transformations.property.mathexpression;

import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction
import eu.esdihumboldt.hale.io.xslt.XslPropertyTransformation
import eu.esdihumboldt.hale.io.xslt.functions.XslFunction
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractXslTransformation


/**
 * XSLT representation of the Rename function. It delegates to different kinds
 * of {@link XslFunction} depending on the cell configuration.
 * 
 * @author Simon Templer
 */
class XslMathExpression extends AbstractXslTransformation implements XslPropertyTransformation,
RenameFunction {

	/**
	 * Simple value rename function
	 */
	//	private final RenameValue value = new RenameValue()


	@Override
	public XslFunction selectFunction(Cell cell) {
		//		use (CellUtil) {
		//			def structuralRename = cell.getOptionalParameter(
		//					PARAMETER_STRUCTURAL_RENAME, Value.of(false)).as(Boolean)
		//
		//			if (!structuralRename) {
		//				// copy value only
		//				value
		//			}
		//			else {
		//				// copy structure
		//				new StructuralRename()
		//			}
		//		}
		null
	}
}
