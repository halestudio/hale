/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.functions.numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.Immutable;

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

/**
 * Mathematical expression evaluation function.
 * @author Simon Templer
 */
@Immutable
public class MathematicalExpression extends AbstractSingleTargetPropertyTransformation<TransformationEngine> {

	/**
	 * Name of the parameter specifying the mathematical expression.
	 */
	public static final String PARAMETER_EXPRESSION = "expression";
	
	/**
	 * Entity name for variables.
	 */
	public static final String ENTITY_VARIABLE = "var";

	/**
	 * @see AbstractSingleTargetPropertyTransformation#evaluate(String, TransformationEngine, ListMultimap, String, PropertyEntityDefinition, Map, TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier,
			TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {
		// get the mathematical expression
		String expression = getParameterChecked(PARAMETER_EXPRESSION);
		
		Environment env = new Environment();
		List<PropertyValue> vars = variables.get(ENTITY_VARIABLE);
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
			
			// the JMEP library only supports Integer and Doubles, but e.g. no Floats
			if (!(number instanceof Integer) && !(number instanceof Double)) {
				number = number.doubleValue();
			}
			
			// determine the variable name
			String name = var.getProperty().getDefinition().getName().getLocalPart();
			Constant varValue = new Constant(number);
			
			// add with short name, but ensure no variable with only a short name is overridden
			if (env.getVariable(name) == null
					|| var.getProperty().getPropertyPath().size() == 1) {
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

		try {
			Expression ex = new Expression(expression, env);
			return ex.evaluate();
		} catch (XExpression e) {
			throw new TransformationException("Error evaluating the cell expression", e);
		}
	}

}
