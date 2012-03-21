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

package eu.esdihumboldt.cst.functions.core;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.springframework.core.convert.ConversionException;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
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
 * @author Simon Templer
 */
public class FormattedString extends
		AbstractSingleTargetPropertyTransformation<TransformationEngine> 
		implements FormattedStringFunction {
	@Override
	protected Object evaluate(String transformationIdentifier,
			TransformationEngine engine,
			ListMultimap<String, PropertyValue> variables, String resultName,
			PropertyEntityDefinition resultProperty,
			Map<String, String> executionParameters, TransformationLog log)
			throws TransformationException, NoResultException {
		String pattern = getParameterChecked(PARAMETER_PATTERN);
		
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
			
			// determine the variable name
			String name = var.getProperty().getDefinition().getName().getLocalPart();
			
			// add with short name, but ensure no variable with only a short name is overridden
			if (!values.keySet().contains(name)
					|| var.getProperty().getPropertyPath().size() == 1) {
				values.put(name, value);
			}
			
			// add with long name if applicable
			if (var.getProperty().getPropertyPath().size() > 1) {
				List<String> names = new ArrayList<String>();
				for (ChildContext context : var.getProperty().getPropertyPath()) {
					names.add(context.getChild().getName().getLocalPart());
				}
				String longName = Joiner.on('.').join(names);
				values.put(longName, value);
			}
		}
		
		// replace markers in pattern
		//FIXME this is quick and dirty! does not handle escaping
		int i = 0;
		for (Entry<String, Object> entry : values.entrySet()) {
			String name = entry.getKey();
			pattern = pattern.replaceAll(
					Pattern.quote("{" + name + "}"), "{" + i + "}");
			i++;
		}
		
		try {
			return MessageFormat.format(pattern, values.values().toArray());
		} catch (IllegalArgumentException e) {
			// missing inputs result in an invalid pattern
			//TODO better way to handle missing inputs
			//FIXME an error should still be reported for invalid patterns
			throw new NoResultException(e);
		}
	}

}
