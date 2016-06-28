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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;
import com.iabcinc.jmep.Environment;
import com.iabcinc.jmep.Expression;
import com.iabcinc.jmep.XExpression;
import com.iabcinc.jmep.hooks.Constant;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import net.jcip.annotations.Immutable;

/**
 * Mathematical expression evaluation function.
 * 
 * @author Simon Templer
 */
@Immutable
public class MathematicalExpression
		extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements MathematicalExpressionFunction {

	/**
	 * @see AbstractSingleTargetPropertyTransformation#evaluate(String,
	 *      TransformationEngine, ListMultimap, String,
	 *      PropertyEntityDefinition, Map, TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException {
		// get the mathematical expression
		String expression = getParameterChecked(PARAMETER_EXPRESSION).as(String.class);

		// replace transformation variables in expression
		expression = getExecutionContext().getVariables().replaceVariables(expression);

		List<PropertyValue> vars = variables.get(ENTITY_VARIABLE);

		try {
			return evaluateExpression(expression, vars);
		} catch (XExpression e) {
			throw new TransformationException("Error evaluating the cell expression", e);
		}
	}

	/**
	 * Evaluate a mathematical expression.
	 * 
	 * @param expression the mathematical expression. It may contain references
	 *            to variables
	 * @param vars the list of available property values that may be bound to
	 *            variables
	 * @return the evaluated expression, which can be Double, Integer or String
	 * @throws XExpression if the expression could not be evaluated
	 */
	public static Object evaluateExpression(String expression, List<PropertyValue> vars)
			throws XExpression {
		Environment env = new Environment();

		for (PropertyValue var : vars) {
			// add the variable to the environment

			// determine the variable value
			Object value = var.getValue();
			Number number;
			if (value instanceof Number) {
				number = (Number) value;
			}
			else {
				// try conversion to Double as default
				number = var.getValueAs(Double.class);
			}

			// the JMEP library only supports Integer and Doubles, but e.g. no
			// Floats
			if (!(number instanceof Integer) && !(number instanceof Double)) {
				number = number.doubleValue();
			}

			// determine the variable name
			String name = var.getProperty().getDefinition().getName().getLocalPart();
			Constant varValue = new Constant(number);

			// add with short name, but ensure no variable with only a short
			// name is overridden
			if (env.getVariable(name) == null || var.getProperty().getPropertyPath().size() == 1) {
				env.addVariable(name, varValue);
			}

			// add with long name if applicable
			if (var.getProperty().getPropertyPath().size() > 1) {
				List<String> names = new ArrayList<String>();
				for (ChildContext context : var.getProperty().getPropertyPath()) {
					names.add(context.getChild().getName().getLocalPart());
				}
				String longName = Joiner.on('.').join(names);
				env.addVariable(longName, varValue);
			}
		}

		Expression ex = new Expression(expression, env);
		return ex.evaluate();
	}

}
