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
import eu.esdihumboldt.cst.functions.groovy.helper.ContextAwareHelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperContext;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunction;
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory;
import eu.esdihumboldt.cst.functions.groovy.helper.spec.Specification;

/**
 * Wrapper for {@link ContextAwareHelperFunction} to associate the function
 * name, the helper context and let it implement
 * {@link HelperFunctionOrCategory}.
 * 
 * @author Simon Templer
 * @param <R> the function return type
 */
public class HelperFunctionContextWrapper<R> implements HelperFunctionOrCategory {

	private final HelperFunction<R> function;
	private final String name;

	/**
	 * Create a new function wrapper.
	 * 
	 * @param function the function
	 * @param name the function name
	 * @param context the helper context
	 */
	public HelperFunctionContextWrapper(final ContextAwareHelperFunction<R> function, String name,
			final HelperContext context) {
		super();
		// function that passes the context on
		this.function = new HelperFunction<R>() {

			@Override
			public R call(Object arg) throws Exception {
				return function.call(arg, context);
			}

			@Override
			public Specification getSpec(String functionName) throws Exception {
				return function.getSpec(functionName);
			}
		};
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
