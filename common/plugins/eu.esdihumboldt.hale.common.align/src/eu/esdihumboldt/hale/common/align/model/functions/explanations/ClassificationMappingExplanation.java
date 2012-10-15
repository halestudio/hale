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

package eu.esdihumboldt.hale.common.align.model.functions.explanations;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for classification mapping cells.
 * 
 * @author Kai Schwierczek
 */
public class ClassificationMappingExplanation extends AbstractCellExplanation implements
		ClassificationMappingFunction {

	private static final String EXPLANATION_PATTERN = "Populates the {0} property from the {1} property with values according to the following mapping:\n"
			+ "{2}\nNot mapped source values will result in the following target value: {3}.";

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		List<ParameterValue> mappings = cell.getTransformationParameters().get(
				PARAMETER_CLASSIFICATIONS);
		String notClassifiedAction = CellUtil.getFirstRawParameter(cell,
				PARAMETER_NOT_CLASSIFIED_ACTION);

		if (target != null && source != null) {
			StringBuilder mappingString = new StringBuilder();
			for (ParameterValue value : mappings) {
				String s = value.getValue();
				try {
					mappingString.append(quoteText(
							URLDecoder.decode(s.substring(0, s.indexOf(' ')), "UTF-8"), false));
				} catch (UnsupportedEncodingException e) {
					// UTF-8 is everywhere
				}
				mappingString.append(" when source value is one of ");
				String[] splitted = s.split(" ");
				for (int i = 1; i < splitted.length; i++) {
					if (i != 1)
						mappingString.append(", ");
					try {
						mappingString.append(quoteText(URLDecoder.decode(splitted[i], "UTF-8"),
								false));
					} catch (UnsupportedEncodingException e) {
						// UTF-8 is everywhere
					}
				}
				mappingString.append(".\n");
			}
			String notClassifiedResult = "null";
			if (USE_SOURCE_ACTION.equals(notClassifiedAction))
				notClassifiedResult = "the source value";
			else if (notClassifiedAction != null
					&& notClassifiedAction.startsWith(USE_FIXED_VALUE_ACTION_PREFIX))
				notClassifiedResult = quoteText(
						notClassifiedAction.substring(notClassifiedAction.indexOf(':') + 1), false);
			// otherwise it's null or USE_NULL_ACTION

			return MessageFormat.format(EXPLANATION_PATTERN, formatEntity(target, false, true),
					formatEntity(source, false, true), mappingString.toString(),
					notClassifiedResult);
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanationAsHtml(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		Entity source = CellUtil.getFirstEntity(cell.getSource());

		List<ParameterValue> mappings = cell.getTransformationParameters().get(
				PARAMETER_CLASSIFICATIONS);
		String notClassifiedAction = CellUtil.getFirstRawParameter(cell,
				PARAMETER_NOT_CLASSIFIED_ACTION);

		if (target != null && source != null) {
			StringBuilder mappingString = new StringBuilder();
			mappingString
					.append("<table border=\"1\"><tr><th>Target value</th><th>Source values</th></tr>");
			for (ParameterValue value : mappings) {
				String s = value.getValue();
				mappingString.append("<tr><td>");
				try {
					mappingString.append(quoteText(
							URLDecoder.decode(s.substring(0, s.indexOf(' ')), "UTF-8"), true));
				} catch (UnsupportedEncodingException e) {
					// UTF-8 is everywhere
				}
				mappingString.append("</td><td>");
				String[] splitted = s.split(" ");
				for (int i = 1; i < splitted.length; i++) {
					if (i != 1)
						mappingString.append(", ");
					try {
						mappingString.append(quoteText(URLDecoder.decode(splitted[i], "UTF-8"),
								true));
					} catch (UnsupportedEncodingException e) {
						// UTF-8 is everywhere
					}
				}
				mappingString.append("</td></tr>");
			}
			mappingString.append("</table>");
			String notClassifiedResult = "null";
			if (USE_SOURCE_ACTION.equals(notClassifiedAction))
				notClassifiedResult = "the source value";
			else if (notClassifiedAction != null
					&& notClassifiedAction.startsWith(USE_FIXED_VALUE_ACTION_PREFIX))
				notClassifiedResult = quoteText(
						notClassifiedAction.substring(notClassifiedAction.indexOf(':') + 1), true);
			// otherwise it's null or USE_NULL_ACTION

			return MessageFormat
					.format(EXPLANATION_PATTERN, formatEntity(target, true, true),
							formatEntity(source, true, true), mappingString.toString(),
							notClassifiedResult).replaceAll("\n", "<br />");
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		if (html)
			return getExplanationAsHtml(cell);
		else
			return getExplanation(cell);
	}
}
