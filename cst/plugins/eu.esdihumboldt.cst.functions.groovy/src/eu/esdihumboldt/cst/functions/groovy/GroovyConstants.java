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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.groovy;

/**
 * Constants regarding the groovy function.
 * 
 * @author Simon Templer
 */
public interface GroovyConstants {

	/**
	 * Name for the parameter containing the groovy script.
	 */
	public static final String PARAMETER_SCRIPT = "script";

	/**
	 * Name of the execution context variable holding the compiled script.
	 */
	public static final String CONTEXT_SCRIPT = "script";

	/**
	 * Entity name for variables.
	 */
	public static final String ENTITY_VARIABLE = "var";

	/**
	 * Name of the instance builder variable in the binding.
	 */
	public static final String BINDING_BUILDER = "_b";

	/**
	 * Name of the source instance variable in the binding (not applicable for
	 * {@link GroovyCreate}).
	 */
	public static final String BINDING_SOURCE = "_source";

	/**
	 * Name of the index variable in the binding (only applicable for
	 * {@link GroovyCreate}).
	 */
	public static final String BINDING_INDEX = "_index";

	/**
	 * Name of the target instance builder closure in the binding.
	 */
	public static final String BINDING_TARGET = "_target";

	/**
	 * Name of the helper functions accessor.
	 */
	public static final String BINDING_HELPER_FUNCTIONS = "_";

	/**
	 * Name of the top level source type entities variable in the binding (not
	 * applicable for {@link GroovyCreate}).
	 */
	public static final String BINDING_SOURCE_TYPES = "_sourceTypes";

	/**
	 * Name of the top level target type entity variable in the binding.
	 */
	public static final String BINDING_TARGET_TYPE = "_targetType";

	/**
	 * Name of the cell variable in the binding.
	 */
	public static final String BINDING_CELL = "_cell";

	/**
	 * Name of the transformation log variable in the binding.
	 */
	public static final String BINDING_LOG = "_log";

	/**
	 * Name of the project variable in the binding.
	 */
	public static final String BINDING_PROJECT = "_project";

	/**
	 * Name of the cell context variable in the binding.
	 */
	public static final String BINDING_CELL_CONTEXT = "withCellContext";

	/**
	 * Name of the function context variable in the binding.
	 */
	public static final String BINDING_FUNCTION_CONTEXT = "withFunctionContext";

	/**
	 * Name of the transformation context variable in the binding.
	 */
	public static final String BINDING_TRANSFORMATION_CONTEXT = "withTransformationContext";
}
