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

package eu.esdihumboldt.hale.common.scripting.scripts.mathematical;

import javax.script.ScriptException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.iabcinc.jmep.Environment;
import com.iabcinc.jmep.Expression;
import com.iabcinc.jmep.XExpression;
import com.iabcinc.jmep.hooks.Constant;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.scripting.Script;

/**
 * Mathematical formula script implementation.
 * 
 * @author Kai Schwierczek
 */
public class MathScript implements Script {

	/**
	 * @see eu.esdihumboldt.hale.common.scripting.Script#evaluate(java.lang.String,
	 *      java.lang.Iterable)
	 */
	@Override
	public Object evaluate(String script, Iterable<PropertyValue> variables) throws ScriptException {
		Object result;
		try {
			Expression ex = new Expression(script, createEnvironment(variables));
			result = ex.evaluate();
		} catch (XExpression e) {
			throw new ScriptException(e); // XXX message + exception?
		}
		return result;
	}

	private Environment createEnvironment(Iterable<PropertyValue> variables) {
		Environment env = new Environment();

		for (PropertyValue var : variables) {
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

			Constant varValue = new Constant(number);
			// add with short name, if it does not override something
			String name = var.getProperty().getDefinition().getName().getLocalPart();
			if (!env.getVariables().containsKey(name))
				env.addVariable(name, varValue);

			// add with full name
			env.addVariable(getVariableName(var.getProperty()), varValue);
		}

		return env;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.scripting.Script#getVariableName(eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition)
	 */
	@Override
	public String getVariableName(PropertyEntityDefinition entityDefinition) {
		return Joiner.on('.').join(
				Lists.transform(entityDefinition.getPropertyPath(),
						new Function<ChildContext, String>() {

							/**
							 * @see com.google.common.base.Function#apply(java.lang.Object)
							 */
							@Override
							public String apply(ChildContext input) {
								return input.getChild().getName().getLocalPart();
							}
						}));
	}

	/**
	 * @see eu.esdihumboldt.hale.common.scripting.Script#validate(java.lang.String,
	 *      java.lang.Iterable)
	 */
	@Override
	public String validate(String script, Iterable<PropertyValue> variables) {
		try {
			new Expression(script, createEnvironment(variables));
		} catch (XExpression e) {
			return e.getLocalizedMessage();
		}

		return null;
	}

}
