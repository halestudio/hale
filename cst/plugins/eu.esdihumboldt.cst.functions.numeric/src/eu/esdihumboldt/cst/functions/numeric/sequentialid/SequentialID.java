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

package eu.esdihumboldt.cst.functions.numeric.sequentialid;

import java.util.Map;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Function that generates a sequential identifier.
 * 
 * @author Simon Templer
 */
public class SequentialID extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements SequentialIDConstants {

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
		// get parameter values
		String prefix = getOptionalParameter(PARAM_PREFIX, Value.of("")).as(String.class);
		String suffix = getOptionalParameter(PARAM_SUFFIX, Value.of("")).as(String.class);
		String startValue = getOptionalParameter(PARAM_START_VALUE, Value.of("1")).as(String.class);

		// replace transformation variables in prefix, suffix and startValue
		prefix = getExecutionContext().getVariables().replaceVariables(prefix);
		suffix = getExecutionContext().getVariables().replaceVariables(suffix);
		startValue = getExecutionContext().getVariables().replaceVariables(startValue);

		// assume type as default for sequence
		String sequenceStr = getOptionalParameter(PARAM_SEQUENCE, Value.of(Sequence.type.name()))
				.as(String.class);

		// select appropriate context and key
		Sequence sequence = Sequence.valueOf(sequenceStr);
		Map<Object, Object> context;
		String key;
		switch (sequence) {
		case overall:
			// use function context
			context = getExecutionContext().getFunctionContext();
			key = PARAM_SEQUENCE;
			break;
		case type:
		default:
			// use cell context
			context = getExecutionContext().getCellContext();
			key = getTargetType().getName().toString();
			break;
		}

		long id;
		synchronized (context) {
			Long seqValue = (Long) context.get(key);
			if (seqValue != null) {
				id = seqValue + 1;
			}
			else {
				if (Integer.parseInt(startValue) == 0) {
					throw new TransformationException(
							"ERROR: the starting value for ID creation should be larger than 0");
					// anyways, in the UI the user cannot use 0s or special
					// characters for startValue
				}
				try {
					id = Integer.parseInt(startValue);
				} catch (Exception e) {
					throw new TransformationException(
							"ERROR: the input value of startValue  " + startValue
									+ " for ID creation: the input value should be an integer");
				}
			}
			context.put(key, id);
		}

		if (prefix.isEmpty() && suffix.isEmpty()) {
			return id;
		}
		else {
			return prefix + id + suffix;
		}
	}
}
