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

package eu.esdihumboldt.cst.functions.groovy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;

/**
 * Property transformation based on a Groovy script.
 * 
 * @author Simon Templer
 */
public class GroovyTransformation extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> {

	/**
	 * Name for the parameter containing the groovy script.
	 */
	public static final String PARAMETER_SCRIPT = "script";

	/**
	 * Entity name for variables.
	 */
	public static final String ENTITY_VARIABLE = "var";

	/**
	 * @see AbstractSingleTargetPropertyTransformation#evaluate(String,
	 *      TransformationEngine, ListMultimap, String,
	 *      PropertyEntityDefinition, Map, TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		// get the mathematical expression
		String script = getParameterChecked(PARAMETER_SCRIPT);

		Binding binding = new Binding() {

			@Override
			public Object getVariable(String name) {
				try {
					return super.getVariable(name);
				} catch (MissingPropertyException mpe) {
					// use null value for variables that are not defined
					return null;
				}
			}

		};
		List<PropertyValue> vars = variables.get(ENTITY_VARIABLE);
		for (PropertyValue var : vars) {
			// add the variable to the environment

			// determine the variable value
			Object value = var.getValue();
			if (value instanceof Instance) {
				value = ((Instance) value).getValue(); // XXX check if there are
														// any properties?
			}
			if (value instanceof Number) {
				// use numbers as is
			}
			else {
				// try conversion to String as default
				value = var.getValueAs(String.class);
			}

			// determine the variable name
			String name = var.getProperty().getDefinition().getName().getLocalPart();

			// add with short name, but ensure no variable with only a short
			// name is overridden
			if (binding.getVariables().get(name) == null
					|| var.getProperty().getPropertyPath().size() == 1) {
				binding.setVariable(name, value);
			}

			// add with long name if applicable
			if (var.getProperty().getPropertyPath().size() > 1) {
				List<String> names = new ArrayList<String>();
				for (ChildContext context : var.getProperty().getPropertyPath()) {
					names.add(context.getChild().getName().getLocalPart());
				}
				String longName = Joiner.on('_').join(names);
				binding.setVariable(longName, value);
			}
		}

		Object result;
		try {
			GroovyShell shell = new GroovyShell(binding);
			result = shell.evaluate(script);
		} catch (Throwable e) {
			throw new TransformationException("Error evaluating the cell script", e);
		}

		if (result == null) {
			throw new NoResultException();
		}
		return result;
	}

}
