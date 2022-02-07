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

package eu.esdihumboldt.cst.functions.string;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Property date extraction function.
 * 
 * @author Kai Schwierczek
 */
public class DateExtraction extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements DateExtractionFunction {

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation#evaluate(java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine,
	 *      com.google.common.collect.ListMultimap, java.lang.String,
	 *      eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition,
	 *      java.util.Map,
	 *      eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException {
		if (getParameters() == null || getParameters().get(PARAMETER_DATE_FORMAT) == null
				|| getParameters().get(PARAMETER_DATE_FORMAT).isEmpty()) {
			throw new TransformationException(MessageFormat
					.format("Mandatory parameter {0} not defined", PARAMETER_DATE_FORMAT));
		}

		String dateFormat = getParameters().get(PARAMETER_DATE_FORMAT).get(0).as(String.class);

		// replace transformation variables in date format
		dateFormat = getExecutionContext().getVariables().replaceVariables(dateFormat);

		String sourceString = variables.values().iterator().next().getValueAs(String.class);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

		// by default leniency is true
		boolean leniency = getOptionalParameter(PARAMETER_LENIENCY, Value.of(true))
				.as(Boolean.class);

		sdf.setLenient(leniency);

		try {
			return sdf.parse(sourceString);
		} catch (ParseException pe) {
			throw new TransformationException("Error parsing the source string", pe);
		}
	}
}
