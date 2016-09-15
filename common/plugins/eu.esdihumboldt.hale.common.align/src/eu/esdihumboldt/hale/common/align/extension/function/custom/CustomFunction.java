/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.extension.function.custom;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;

/**
 * Custom function interface.
 * 
 * @param <T> the transformation function type
 * @param <F> the function type
 * @author Simon Templer
 */
public interface CustomFunction<F extends FunctionDefinition<?>, T extends TransformationFunction<?>> {

	/**
	 * @return the function descriptor
	 */
	F getDescriptor();

	/**
	 * @return the transformation function implementation
	 */
	T createTransformationFunction();

}
