/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.groovy.helper;

/**
 * Extended helper function interface for helpers that support/require context
 * information.
 * 
 * @author Simon Templer
 * @param <R> the function return type
 */
public interface ContextAwareHelperFunction<R> extends HelperFunction<R> {

	/**
	 * Call the function.
	 * 
	 * @param arg the single function argument or a Map of parameter names to
	 *            parameter values (to be called as named parameters)
	 * @param context the helper context
	 * @return the function result
	 * @throws Exception if the function call fails due to an exception
	 */
	R call(Object arg, HelperContext context) throws Exception;

	@Override
	default R call(Object arg) throws Exception {
		return call(arg, null);
	}

}
