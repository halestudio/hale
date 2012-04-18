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

/**
 * Property date extraction function.
 * 
 * @author Kai Schwierczek
 */
public class DateExtraction extends AbstractSingleTargetPropertyTransformation<TransformationEngine> implements DateExtractionFunction {
	
	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation#evaluate(java.lang.String, eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine, com.google.common.collect.ListMultimap, java.lang.String, eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition, java.util.Map, eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog)
	 */
	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName, PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log) throws TransformationException {
		if (getParameters() == null
				|| getParameters().get(PARAMETER_DATE_FORMAT) == null
				|| getParameters().get(PARAMETER_DATE_FORMAT).isEmpty()) {
			throw new TransformationException(MessageFormat.format(
					"Mandatory parameter {0} not defined", PARAMETER_DATE_FORMAT));
		}
		
		String dateFormat = getParameters().get(PARAMETER_DATE_FORMAT).get(0);
		String sourceString = variables.values().iterator().next().getValueAs(String.class);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		
		try {
			return sdf.parse(sourceString);
		} catch (ParseException pe) {
			throw new TransformationException("Error parsing the source string", pe);
		}
	}
}
