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

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Abstract property transformation implementation for functions with a single
 * target property. Supports scripted function parameters.
 * 
 * @author Kai Schwierczek
 * @param <E> the transformation engine type
 */
public abstract class AbstractSingleTargetScriptedPropertyTransformation<E extends TransformationEngine>
		extends AbstractScriptedPropertyTransformation<E> {

	@Override
	protected ListMultimap<String, Object> evaluateImpl(String transformationIdentifier, E engine,
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
