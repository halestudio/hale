/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Regex string analysis function.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class RegexAnalysis extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements RegexAnalysisFunction {

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
		if (getParameters() == null || getParameters().get(PARAMETER_REGEX_PATTERN) == null
				|| getParameters().get(PARAMETER_REGEX_PATTERN).isEmpty()) {
			throw new TransformationException(MessageFormat.format(
					"Mandatory parameter {0} not defined", PARAMETER_REGEX_PATTERN));
		}
		if (getParameters() == null || getParameters().get(PARAMETER_OUTPUT_FORMAT) == null
				|| getParameters().get(PARAMETER_OUTPUT_FORMAT).isEmpty()) {
			throw new TransformationException(MessageFormat.format(
					"Mandatory parameter {0} not defined", PARAMETER_OUTPUT_FORMAT));
		}

		String regexPattern = getParameters().get(PARAMETER_REGEX_PATTERN).get(0).as(String.class);
		String outputFormat = getParameters().get(PARAMETER_OUTPUT_FORMAT).get(0).as(String.class);

		String sourceString = variables.values().iterator().next().getValueAs(String.class);

		outputFormat = analize(regexPattern, outputFormat, sourceString);

		return outputFormat;
	}

	/**
	 * Performs regex analysis.
	 * 
	 * @param regexPattern the regular expression.
	 * @param outputFormat the output format to gain.
	 * @param sourceString the text to convert.
	 * @return the converted text.
	 */
	public static String analize(String regexPattern, String outputFormat, String sourceString) {
		try {
			Pattern pattern = Pattern.compile(regexPattern);
			Matcher matcher = pattern.matcher(sourceString);

			HashMap<Integer, String> groupsMap = new HashMap<Integer, String>();
			int index = 1;
			if (matcher.find()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String substring = sourceString.substring(matcher.start(i), matcher.end(i));
					groupsMap.put(index, substring);
					index++;
				}
			}

			Collection<Entry<Integer, String>> entries = groupsMap.entrySet();
			for (Entry<Integer, String> entry : entries) {
				Integer key = entry.getKey();
				String value = entry.getValue();
				outputFormat = outputFormat.replaceAll("\\{" + key + "\\}", value);
			}
			return outputFormat;
		} catch (Exception e) {
			e.printStackTrace();

			return "Unable to convert";
		}
	}
}
