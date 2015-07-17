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

package eu.esdihumboldt.cst.functions.groovy.helper.extension;

import eu.esdihumboldt.cst.functions.groovy.helper.Category;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;

/**
 * Simple wrapper for {@link HelperFunction} to associate the function name and
 * let it implement {@link HelperFunctionOrCategory}.
 * 
 * @author Simon Templer
 */
public class HelperFunctionWrapper implements HelperFunctionOrCategory {

	private final HelperFunction<?> function;
	private final String name;

	/**
	 * Create a new function wrapper.
	 * 
	 * @param function the function
	 * @param name the function name
	 */
	public HelperFunctionWrapper(HelperFunction<?> function, String name) {
		super();
		this.function = function;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Category asCategory() {
		return null;
	}

	@Override
	public HelperFunction<?> asFunction() {
		return function;
	}

}
