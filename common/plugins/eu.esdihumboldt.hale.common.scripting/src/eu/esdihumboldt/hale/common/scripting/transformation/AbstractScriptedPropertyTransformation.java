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

package eu.esdihumboldt.hale.common.scripting.transformation;

import java.text.MessageFormat;
import java.util.Map;

import javax.script.ScriptException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.scripting.Script;
import eu.esdihumboldt.hale.common.scripting.ScriptExtension;
import eu.esdihumboldt.hale.common.scripting.ScriptFactory;

/**
 * Base class for {@link PropertyTransformation} that supports evaluating
 * scripted parameters.
 * 
 * @param <E> the transformation engine type
 * @author Kai Schwierczek
 */
public abstract class AbstractScriptedPropertyTransformation<E extends TransformationEngine>
		extends AbstractPropertyTransformation<E> {

	private ListMultimap<String, Value> transformedParameters;

	@Override
	protected final ListMultimap<String, Object> evaluate(String transformationIdentifier, E engine,
			ListMultimap<String, PropertyValue> variables,
			ListMultimap<String, PropertyEntityDefinition> resultNames,
			Map<String, String> executionParameters, TransformationLog log)
					throws TransformationException {
		ListMultimap<String, ParameterValue> originalParameters = getParameters();
		ListMultimap<String, Value> transformedParameters = ArrayListMultimap.create();

		if (originalParameters != null) {
			for (Map.Entry<String, ParameterValue> entry : originalParameters.entries()) {
				if (!entry.getValue().needsProcessing())
					transformedParameters.put(entry.getKey(), entry.getValue().intern());
				else {
					// type is a script
					ScriptFactory factory = ScriptExtension.getInstance()
							.getFactory(entry.getValue().getType());
					if (factory == null)
						throw new TransformationException("Couldn't find factory for script id "
								+ entry.getValue().getType());
					Script script;
					try {
						script = factory.createExtensionObject();
					} catch (Exception e) {
						throw new TransformationException("Couldn't create script from factory", e);
					}
					Object result;
					try {
						String scriptStr = entry.getValue().as(String.class);
						if (script.requiresReplacedTransformationVariables()) {
							// replace transformation variables
							scriptStr = getExecutionContext().getVariables()
									.replaceVariables(scriptStr);
						}
						result = script.evaluate(scriptStr, variables.values(),
								getExecutionContext());
					} catch (ScriptException e) {
						throw new TransformationException(
								"Couldn't evaluate a transformation parameter", e);
					}
					// XXX use conversion service instead of valueOf?
					transformedParameters.put(entry.getKey(), Value.simple(result));
				}
			}
		}

		this.transformedParameters = Multimaps.unmodifiableListMultimap(transformedParameters);

		return evaluateImpl(transformationIdentifier, engine, variables, resultNames,
				executionParameters, log);
	}

	/**
	 * Execute the evaluation function as configured. The transformed parameters
	 * are available in here.
	 * 
	 * @param transformationIdentifier the transformation function identifier
	 * @param engine the transformation engine that may be used for the function
	 *            execution
	 * @param variables the input variables
	 * @param resultNames the expected results (names associated with the
	 *            corresponding entity definitions)
	 * @param executionParameters additional parameters for the execution, may
	 *            be <code>null</code>
	 * @param log the transformation log to report any information about the
	 *            execution of the transformation to
	 * @return the evaluation result
	 * @throws TransformationException if an unrecoverable error occurs during
	 *             transformation
	 */
	protected abstract ListMultimap<String, Object> evaluateImpl(String transformationIdentifier,
			E engine, ListMultimap<String, PropertyValue> variables,
			ListMultimap<String, PropertyEntityDefinition> resultNames,
			Map<String, String> executionParameters, TransformationLog log)
					throws TransformationException;

	/**
	 * Returns the transformed parameters.
	 * 
	 * @return the transformed parameters
	 */
	protected ListMultimap<String, Value> getTransformedParameters() {
		return transformedParameters;
	}

	/**
	 * Get the first evaluated parameter defined with the given parameter name.
	 * Throws a {@link TransformationException} if such a parameter doesn't
	 * exist.
	 * 
	 * @param parameterName the parameter name
	 * @return the parameter value
	 * @throws TransformationException if a parameter with the given name
	 *             doesn't exist
	 */
	protected Value getTransformedParameterChecked(String parameterName)
			throws TransformationException {
		if (transformedParameters == null || transformedParameters.get(parameterName) == null
				|| transformedParameters.get(parameterName).isEmpty()) {
			throw new TransformationException(
					MessageFormat.format("Mandatory parameter {0} not defined", parameterName));
		}

		return transformedParameters.get(parameterName).get(0);
	}

	/**
	 * Get the first evaluated parameter defined with the given parameter name.
	 * If no such parameter exists, the given default value is returned.
	 * 
	 * @param parameterName the parameter name
	 * @param defaultValue the default value for the parameter
	 * @return the parameter value, or the default if none is specified
	 */
	protected Value getTransformedOptionalParameter(String parameterName, Value defaultValue) {
		if (transformedParameters == null || transformedParameters.get(parameterName) == null
				|| transformedParameters.get(parameterName).isEmpty()) {
			return defaultValue;
		}

		return transformedParameters.get(parameterName).get(0);
	}
}
