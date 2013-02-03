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

import java.text.MessageFormat;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.common.align.model.functions.ClassificationMappingUtil;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.lookup.LookupTable;

/**
 * Explanation for classification mapping cells.
 * 
 * @author Kai Schwierczek
 */
public class ClassificationMappingExplanation extends AbstractCellExplanation implements
		ClassificationMappingFunction {

	private static final String EXPLANATION_PATTERN = "Populates the {0} property from the {1} property with values according to the following mapping:\n"
			+ "{2}\nNot mapped source values will result in the following target value: {3}.";

	@Override
	public String getExplanation(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		// FIXME provide service provider!
		LookupTable lookup = ClassificationMappingUtil.getClassificationLookup(
				cell.getTransformationParameters(), null);
		ListMultimap<Value, Value> revLookup = lookup.reverse();
		String notClassifiedAction = CellUtil.getFirstParameter(cell,
				PARAMETER_NOT_CLASSIFIED_ACTION).as(String.class);

		if (target != null && source != null) {
			StringBuilder mappingString = new StringBuilder();
			for (Value targetValue : revLookup.keySet()) {
				mappingString.append(quoteText(targetValue.as(String.class), false));
				mappingString.append(" when source value is one of ");
				int i = 1;
				for (Value sourceValue : revLookup.get(targetValue)) {
					if (i != 1) {
						mappingString.append(", ");
					}
					mappingString.append(quoteText(sourceValue.as(String.class), false));

					i++;
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

	@Override
	public String getExplanationAsHtml(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		Entity source = CellUtil.getFirstEntity(cell.getSource());

		// FIXME provide service provider!
		LookupTable lookup = ClassificationMappingUtil.getClassificationLookup(
				cell.getTransformationParameters(), null);
		ListMultimap<Value, Value> revLookup = lookup.reverse();
		String notClassifiedAction = CellUtil.getFirstParameter(cell,
				PARAMETER_NOT_CLASSIFIED_ACTION).as(String.class);

		if (target != null && source != null) {
			StringBuilder mappingString = new StringBuilder();
			mappingString
					.append("<table border=\"1\"><tr><th>Target value</th><th>Source values</th></tr>");
			for (Value targetValue : revLookup.keySet()) {
				mappingString.append("<tr><td>");
				mappingString.append(quoteText(targetValue.as(String.class), true));
				mappingString.append("</td><td>");
				int i = 1;
				for (Value sourceValue : revLookup.get(targetValue)) {
					if (i != 1) {
						mappingString.append(", ");
					}
					mappingString.append(quoteText(sourceValue.as(String.class), true));
					i++;
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

	@Override
	protected String getExplanation(Cell cell, boolean html) {
		if (html)
			return getExplanationAsHtml(cell);
		else
			return getExplanation(cell);
	}
}
