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
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Abstract property transformation implementation for functions with a single
 * target property.
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractSingleTargetPropertyTransformation<E extends TransformationEngine>
		extends AbstractPropertyTransformation<E> {

	/**
	 * @see AbstractPropertyTransformation#evaluate(String,
	 *      TransformationEngine, ListMultimap, ListMultimap, Map,
	 *      TransformationLog)
	 */
	@Override
	protected ListMultimap<String, Object> evaluate(String transformationIdentifier, E engine,
			ListMultimap<String, PropertyValue> variables,
			ListMultimap<String, PropertyEntityDefinition> resultNames,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException {
		assert resultNames.size() == 1;
		Entry<String, PropertyEntityDefinition> entry = resultNames.entries().iterator().next();
		ListMultimap<String, Object> resultMap = ArrayListMultimap.create(1, 1);
		try {
			Object result = evaluate(transformationIdentifier, engine, variables, entry.getKey(),
					entry.getValue(), executionParameters, log);
			resultMap.put(entry.getKey(), result);
		} catch (NoResultException e) {
			// no result returned
			// /TODO warning? or ignore?
		}
		return resultMap;
	}

	/**
	 * Execute the evaluation function as configured.
	 * 
	 * @param transformationIdentifier the transformation function identifier
	 * @param engine the transformation engine that may be used for the function
	 *            execution
	 * @param variables the input variables
	 * @param resultName the name of the result
	 * @param resultProperty the property entity definition associated with the
	 *            result
	 * @param executionParameters additional parameters for the execution, may
	 *            be <code>null</code>
	 * @param log the transformation log to report any information about the
	 *            execution of the transformation to
	 * @return the evaluation result
	 * @throws TransformationException if an unrecoverable error occurs during
	 *             transformation
	 * @throws NoResultException if the function does not yield a result
	 */
	protected abstract Object evaluate(String transformationIdentifier, E engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException;

}
