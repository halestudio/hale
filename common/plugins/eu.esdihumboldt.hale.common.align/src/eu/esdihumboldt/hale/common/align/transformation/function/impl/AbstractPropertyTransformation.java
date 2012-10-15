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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

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
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.scripting.Script;
import eu.esdihumboldt.hale.common.scripting.ScriptExtension;
import eu.esdihumboldt.hale.common.scripting.ScriptFactory;

/**
 * Base class for implementing {@link PropertyTransformation}s
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractPropertyTransformation<E extends TransformationEngine> extends
		AbstractTransformationFunction<E> implements PropertyTransformation<E> {

	private ListMultimap<String, Object> results;
	private ListMultimap<String, PropertyValue> variables;
	private ListMultimap<String, PropertyEntityDefinition> resultNames;
	private ListMultimap<String, String> transformedParameters;
	private TypeDefinition targetType;

	/**
	 * @see PropertyTransformation#setTargetType(TypeDefinition)
	 */
	@Override
	public void setTargetType(TypeDefinition targetType) {
		this.targetType = targetType;
	}

	/**
	 * Get the target type of the instance that is to be populated with the
	 * function result.
	 * 
	 * @return the target instance type
	 */
	protected TypeDefinition getTargetType() {
		return targetType;
	}

	/**
	 * @see PropertyTransformation#setVariables(ListMultimap)
	 */
	@Override
	public void setVariables(ListMultimap<String, PropertyValue> variables) {
		this.variables = Multimaps.unmodifiableListMultimap(variables);
	}

	/**
	 * @see PropertyTransformation#getResults()
	 */
	@Override
	public ListMultimap<String, Object> getResults() {
		return results;
	}

	/**
	 * @see PropertyTransformation#setExpectedResult(ListMultimap)
	 */
	@Override
	public void setExpectedResult(ListMultimap<String, PropertyEntityDefinition> resultNames) {
		this.resultNames = Multimaps.unmodifiableListMultimap(resultNames);
	}

	/**
	 * @see TransformationFunction#execute(String, TransformationEngine, Map,
	 *      TransformationLog)
	 */
	@Override
	public void execute(String transformationIdentifier, E engine,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {
		ListMultimap<String, ParameterValue> originalParameters = getParameters();
		ListMultimap<String, String> transformedParameters = ArrayListMultimap.create();

		if (originalParameters != null) {
			for (Map.Entry<String, ParameterValue> entry : originalParameters.entries()) {
				if (ParameterValue.DEFAULT_TYPE.equals(entry.getValue().getType()))
					transformedParameters.put(entry.getKey(), entry.getValue().getValue());
				else {
					// type is a script
					ScriptFactory factory = ScriptExtension.getInstance().getFactory(
							entry.getValue().getType());
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
						result = script.evaluate(entry.getValue().getValue(), variables.values());
					} catch (ScriptException e) {
						throw new TransformationException(
								"Couldn't evaluate a transformation parameter", e);
					}
					// XXX use conversion service instead of valueOf?
					transformedParameters.put(entry.getKey(), String.valueOf(result));
				}
			}
		}

		this.transformedParameters = Multimaps.unmodifiableListMultimap(transformedParameters);

		results = evaluate(transformationIdentifier, engine, variables, resultNames,
				executionParameters, log);
	}

	/**
	 * Execute the evaluation function as configured.
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
	protected abstract ListMultimap<String, Object> evaluate(String transformationIdentifier,
			E engine, ListMultimap<String, PropertyValue> variables,
			ListMultimap<String, PropertyEntityDefinition> resultNames,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException;

	/**
	 * Automatic result conversion allowed by default. Override to change this
	 * behavior.
	 * 
	 * @see PropertyTransformation#allowAutomatedResultConversion()
	 */
	@Override
	public boolean allowAutomatedResultConversion() {
		return true;
	}

	/**
	 * Returns the transformed parameters.
	 * 
	 * @return the transformed parameters
	 */
	protected ListMultimap<String, String> getTransformedParameters() {
		return transformedParameters;
	}

	/**
	 * Get the first parameter defined with the given parameter name. Throws a
	 * {@link TransformationException} if such a parameter doesn't exist.
	 * 
	 * @param parameterName the parameter name
	 * @return the parameter value
	 * @throws TransformationException if a parameter with the given name
	 *             doesn't exist
	 */
	protected String getParameterChecked(String parameterName) throws TransformationException {
		if (transformedParameters == null || transformedParameters.get(parameterName) == null
				|| transformedParameters.get(parameterName).isEmpty()) {
			throw new TransformationException(MessageFormat.format(
					"Mandatory parameter {0} not defined", parameterName));
		}

		return transformedParameters.get(parameterName).get(0);
	}

	/**
	 * Get the first parameter defined with the given parameter name. If no such
	 * parameter exists, the given default value is returned.
	 * 
	 * @param parameterName the parameter name
	 * @param defaultValue the default value for the parameter
	 * @return the parameter value, or the default if none is specified
	 */
	protected String getOptionalParameter(String parameterName, String defaultValue) {
		if (transformedParameters == null || transformedParameters.get(parameterName) == null
				|| transformedParameters.get(parameterName).isEmpty()) {
			return defaultValue;
		}

		return transformedParameters.get(parameterName).get(0);
	}
}
