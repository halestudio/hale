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

package eu.esdihumboldt.cst.functions.core;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.springframework.core.convert.ConversionException;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyValue;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTargetPropertyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.NoResultException;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;

/**
 * Function that creates a formatted string from a pattern and input variables.
 * 
 * @author Simon Templer
 */
public class FormattedString
		extends AbstractSingleTargetPropertyTransformation<TransformationEngine>
		implements FormattedStringFunction {

	@Override
	protected Object evaluate(String transformationIdentifier, TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty, Map<String, String> executionParameters,
			TransformationLog log) throws TransformationException, NoResultException {
		String pattern = getParameterChecked(PARAMETER_PATTERN).as(String.class);

		// replace transformation variables
		pattern = getExecutionContext().getVariables().replaceVariables(pattern);

		// name/value mapping
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		List<PropertyValue> vars = variables.get(ENTITY_VARIABLE);
		for (PropertyValue var : vars) {
			// determine the variable value
			Object value;
			try {
				value = var.getValueAs(String.class);
			} catch (ConversionException e) {
				value = var.getValue();
			}

			FormattedStringFunction.addValue(values, value, var.getProperty());
		}

		// replace markers in pattern
		// FIXME this is quick and dirty! does not handle escaping
		int i = 0;
		for (Entry<String, Object> entry : values.entrySet()) {
			String name = entry.getKey();
			pattern = pattern.replaceAll(Pattern.quote("{" + name + "}"), "{" + i + "}");
			i++;
		}

		try {
			return MessageFormat.format(pattern, values.values().toArray());
		} catch (IllegalArgumentException e) {
			// missing inputs result in an invalid pattern
			// TODO better way to handle missing inputs
			// FIXME an error should still be reported for invalid patterns
			throw new NoResultException(e);
		}
	}

}
