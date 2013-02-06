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

package eu.esdihumboldt.cst.functions.numeric;

/**
 * Mathematical Expression Constants
 * 
 * @author Kevin Mais
 */
public interface MathematicalExpressionFunction {

	/**
	 * the mathematical expression function Id
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.numeric.mathexpression";

	/**
	 * Name of the parameter specifying the mathematical expression.
	 */
	public static final String PARAMETER_EXPRESSION = "expression";

	/**
	 * Entity name for variables.
	 */
	public static final String ENTITY_VARIABLE = "var";

	/**
	 * A regular expression of the set of special characters used to split an
	 * expression.
	 * 
	 * <b>NOTE: add special characters here if necessary or missing.</b>
	 */
	public static final String MATH_SPECIALS = "\\[|\\(|\\)|\\]|\\+|\\-|\\*|\\^|\\/|%";

}
