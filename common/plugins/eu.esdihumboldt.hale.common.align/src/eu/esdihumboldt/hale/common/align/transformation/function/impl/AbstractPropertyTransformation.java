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

import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

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

}
