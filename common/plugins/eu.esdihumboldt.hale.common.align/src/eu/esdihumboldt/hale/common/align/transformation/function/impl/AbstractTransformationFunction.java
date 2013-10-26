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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.ExecutionContext;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Transformation function base class
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationFunction<E extends TransformationEngine> implements
		TransformationFunction<E> {

	private ListMultimap<String, ParameterValue> parameters;
	private ExecutionContext executionContext;

	/**
	 * @see TransformationFunction#setParameters(ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, ParameterValue> parameters) {
		this.parameters = (parameters == null) ? (null) : (Multimaps
				.unmodifiableListMultimap(parameters));
	}

	/**
	 * Get the function parameters
	 * 
	 * @return the parameters, may be <code>null</code> if there are none
	 */
	public ListMultimap<String, ParameterValue> getParameters() {
		return parameters;
	}

	/**
	 * @see TransformationFunction#setExecutionContext(ExecutionContext)
	 */
	@Override
	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	/**
	 * Get the current execution context.
	 * 
	 * @return the executionContext the execution context
	 */
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	/**
	 * Checks if a certain parameter is defined at least a given number of
	 * times. Throws a {@link TransformationException} otherwise.
	 * 
	 * @param parameterName the parameter name
	 * @param minCount the minimum count the parameter must be present
	 * @throws TransformationException if the parameter doesn't exist the given
	 *             number of times
	 */
	public void checkParameter(String parameterName, int minCount) throws TransformationException {
		if (parameters == null || parameters.get(parameterName) == null
				|| parameters.get(parameterName).size() < minCount) {
			if (minCount == 1) {
				throw new TransformationException(MessageFormat.format(
						"Mandatory parameter {0} not defined", parameterName));
			}
			else {
				throw new TransformationException(MessageFormat.format(
						"Parameter {0} is needed at least {1} times", parameterName, minCount));
			}
		}
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
	public ParameterValue getParameterChecked(String parameterName) throws TransformationException {
		if (getParameters() == null || getParameters().get(parameterName) == null
				|| getParameters().get(parameterName).isEmpty()) {
			throw new TransformationException(MessageFormat.format(
					"Mandatory parameter {0} not defined", parameterName));
		}

		return getParameters().get(parameterName).get(0);
	}

	/**
	 * Get the first parameter defined with the given parameter name. If no such
	 * parameter exists, the given default value is returned.
	 * 
	 * @param parameterName the parameter name
	 * @param defaultValue the default value for the parameter
	 * @return the parameter value, or the default if none is specified
	 */
	public ParameterValue getOptionalParameter(String parameterName, Value defaultValue) {
		if (getParameters() == null || getParameters().get(parameterName) == null
				|| getParameters().get(parameterName).isEmpty()
				|| getParameters().get(parameterName).get(0).isEmpty()) {
			return new ParameterValue(defaultValue);
		}

		return getParameters().get(parameterName).get(0);
	}

}
