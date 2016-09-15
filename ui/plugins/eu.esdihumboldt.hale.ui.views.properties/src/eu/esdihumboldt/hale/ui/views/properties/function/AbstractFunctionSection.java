/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.properties.function;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.ui.views.properties.AbstractTextSection;

/**
 * Abstract section for function properties
 * 
 * @author Patrick Lieb
 * @param <F> the function
 */
public abstract class AbstractFunctionSection<F extends FunctionDefinition<?>> extends
		AbstractTextSection {

	/**
	 * the Function for this section
	 */
	private F function;

	/**
	 * @param fun the Function
	 */
	protected void setFunction(F fun) {
		function = fun;
	}

	/**
	 * @return the Function
	 */
	public F getFunction() {
		return function;
	}
}
