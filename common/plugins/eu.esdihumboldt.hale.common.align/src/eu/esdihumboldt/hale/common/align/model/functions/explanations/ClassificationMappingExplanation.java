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

package eu.esdihumboldt.hale.common.align.model.functions.explanations;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;

/**
 * Explanation for classification mapping cells.
 * 
 * @author Kai Schwierczek
 */
public class ClassificationMappingExplanation implements CellExplanation, ClassificationMappingFunction {
	private static final String EXPLANATION_PATTERN = "Populates the ''{0}'' property with values according to the following mapping:\n"
			+ "{1}\nNot mapped source values will result in the following target value: {2}.";

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		List<String> mappings = cell.getTransformationParameters().get(PARAMETER_CLASSIFICATIONS);
		String notClassifiedAction = CellUtil.getFirstParameter(cell, PARAMETER_NOT_CLASSIFIED_ACTION);
		
		if (target != null) {
			StringBuilder mappingString = new StringBuilder();
			for (String s : mappings) {
				mappingString.append('"');
				try {
					mappingString.append(URLDecoder.decode(s.substring(0, s.indexOf(' ')), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// UTF-8 is everywhere
				}
				mappingString.append("\" when source value is one of ");
				String[] splitted = s.split(" ");
				for (int i = 1; i < splitted.length; i++) {
					if (i != 1)
						mappingString.append(", \"");
					else
						mappingString.append("\"");
					try {
						mappingString.append(URLDecoder.decode(splitted[i], "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// UTF-8 is everywhere
					}
					mappingString.append('"');
				}
				mappingString.append(".\n");
			}
			String notClassifiedResult = "null";
			if (USE_SOURCE_ACTION.equals(notClassifiedAction))
				notClassifiedResult = "the source value";
			else if (notClassifiedAction != null && notClassifiedAction.startsWith(USE_FIXED_VALUE_ACTION_PREFIX))
				notClassifiedResult = "\"" + notClassifiedAction.substring(notClassifiedAction.indexOf(':') + 1) + "\"";
			// otherwise it's null or USE_NULL_ACTION

			return MessageFormat.format(EXPLANATION_PATTERN, 
					target.getDefinition().getDefinition().getDisplayName(),
					mappingString.toString(), notClassifiedResult);
		}
		
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanationAsHtml(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		// TODO Auto-generated method stub
		return null;
	}
}
