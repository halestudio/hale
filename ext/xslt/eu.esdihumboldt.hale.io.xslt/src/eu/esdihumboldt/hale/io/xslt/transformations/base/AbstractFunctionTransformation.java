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

package eu.esdihumboldt.hale.io.xslt.transformations.base;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.io.xslt.XslPropertyTransformation;
import eu.esdihumboldt.hale.io.xslt.functions.XslFunction;

/**
 * Property transformation represented by itself as the only function.
 * 
 * @author Simon Templer
 */
public abstract class AbstractFunctionTransformation extends AbstractXslTransformation implements
		XslPropertyTransformation, XslFunction {

	@Override
	public XslFunction selectFunction(Cell cell) throws TransformationException {
		return this;
	}

}
