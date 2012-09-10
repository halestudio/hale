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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.text.MessageFormat;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;

/**
 * Transformation function base class
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractTransformationFunction<E extends TransformationEngine> implements
		TransformationFunction<E> {

	private ListMultimap<String, String> parameters;

	/**
	 * @see TransformationFunction#setParameters(ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		this.parameters = (parameters == null) ? (null) : (Multimaps
				.unmodifiableListMultimap(parameters));
	}

	/**
	 * Get the function parameters
	 * 
	 * @return the parameters, may be <code>null</code> if there are none
	 */
	public ListMultimap<String, String> getParameters() {
		return parameters;
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
	protected void checkParameter(String parameterName, int minCount)
			throws TransformationException {
		if (getParameters() == null || getParameters().get(parameterName) == null
				|| getParameters().get(parameterName).size() < minCount) {
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
	protected String getParameterChecked(String parameterName) throws TransformationException {
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
	protected String getOptionalParameter(String parameterName, String defaultValue) {
		if (getParameters() == null || getParameters().get(parameterName) == null
				|| getParameters().get(parameterName).isEmpty()) {
			return defaultValue;
		}

		return getParameters().get(parameterName).get(0);
	}

}
