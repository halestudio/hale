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

		String prefix = getOptionalParameter(PARAM_PREFIX, "");
		String suffix = getOptionalParameter(PARAM_SUFFIX, "");
		// assume type as default for sequence
		String sequenceStr = getOptionalParameter(PARAM_SEQUENCE, Sequence.type.name());

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
				id = seqValue++;
			}
			else {
				id = START_VALUE;
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
