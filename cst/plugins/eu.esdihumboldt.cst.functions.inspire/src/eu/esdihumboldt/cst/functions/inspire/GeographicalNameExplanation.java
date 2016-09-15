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

package eu.esdihumboldt.cst.functions.inspire;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyFunctionDefinition;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Explanation class for the geographical name function
 * 
 * @author Kevin Mais
 */
public class GeographicalNameExplanation extends AbstractCellExplanation
		implements GeographicalNameFunction {

	@Override
	protected String getExplanation(Cell cell, boolean html, ServiceProvider services,
			Locale locale) {
		// only one locale supported in this explanation (the function is
		// deprecated)
		Locale targetLocale = Locale.ENGLISH;

		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		PropertyFunctionDefinition function = FunctionUtil.getPropertyFunction(ID, null);

		StringBuilder sb = new StringBuilder();
		sb.append(
				"The {0} property is populated with an Inspire Geographical Name composed as follows:");
		addLineBreak(sb, html);
		addLineBreak(sb, html);

		// unique parameters
		if (html)
			sb.append("<ul>");
		addOptionalParameter(sb, cell, PROPERTY_NAMESTATUS, function, html);
		addOptionalParameter(sb, cell, PROPERTY_LANGUAGE, function, html);
		addOptionalParameter(sb, cell, PROPERTY_NATIVENESS, function, html);
		addOptionalParameter(sb, cell, PROPERTY_SOURCEOFNAME, function, html);
		addOptionalParameter(sb, cell, PROPERTY_PRONUNCIATIONIPA, function, html);
		addOptionalParameter(sb, cell, PROPERTY_PRONUNCIATIONSOUNDLINK, function, html);
		addOptionalParameter(sb, cell, PROPERTY_GRAMMA_GENDER, function, html);
		addOptionalParameter(sb, cell, PROPERTY_GRAMMA_NUMBER, function, html);
		if (html)
			sb.append("</ul>");

		addLineBreak(sb, html);

		// per source parameters
		List<? extends Entity> sources = cell.getSource().get(null);
//		PROPERTY_TEXT
		List<ParameterValue> scripts = cell.getTransformationParameters().get(PROPERTY_SCRIPT);
		List<ParameterValue> transs = cell.getTransformationParameters()
				.get(PROPERTY_TRANSLITERATION);

		if (!sources.isEmpty()) {
			sb.append(
					"For each source property a spelling is created, the spelling text is the value of the source property.");
			addLineBreak(sb, html);

			if (html) {
				sb.append("<table border=\"1\">");
				sb.append(
						"<tr><th>Source property</th><th>Script</th><th>Transliteration</th></tr>");
			}

			int index = 0;
			for (Entity source : sources) {
				String script = (index < scripts.size()) ? (scripts.get(index).as(String.class))
						: (null);
				String trans = (index < transs.size()) ? (transs.get(index).as(String.class))
						: (null);

				if (html) {
					sb.append("<tr>");
					sb.append("<td>");
					sb.append(formatEntity(source, html, false, targetLocale));
					sb.append("</td>");
					sb.append("<td>");
					if (script != null) {
						sb.append(script);
					}
					sb.append("</td>");
					sb.append("<td>");
					if (trans != null) {
						sb.append(trans);
					}
					sb.append("</td>");
					sb.append("</tr>");
				}
				else {
					sb.append("Source: ");
					sb.append(formatEntity(source, html, false, targetLocale));
					addLineBreak(sb, html);

					if (script != null && !script.isEmpty()) {
						sb.append("  Script: ");
						sb.append(script);
						addLineBreak(sb, html);
					}

					if (trans != null && !trans.isEmpty()) {
						sb.append("  Transliteration: ");
						sb.append(trans);
						addLineBreak(sb, html);
					}

					addLineBreak(sb, html);
				}

				index++;
			}

			if (html) {
				sb.append("</table>");
			}
		}

		String result = sb.toString();

		if (target != null) {
			result = MessageFormat.format(result, formatEntity(target, html, true, targetLocale));
		}

		return result;
	}

	private void addLineBreak(StringBuilder sb, boolean html) {
		if (html) {
			sb.append("<br />");
		}
		else {
			sb.append('\n');
		}
	}

	private void addOptionalParameter(StringBuilder sb, Cell cell, String paramName,
			PropertyFunctionDefinition function, boolean html) {
		String value = CellUtil.getFirstParameter(cell, paramName).as(String.class);
		if (value != null && !value.isEmpty()) {
			FunctionParameterDefinition param = function.getParameter(paramName);

			if (html) {
				sb.append("<li><i>");
			}
			else {
				sb.append("- ");
			}
			sb.append(param.getDisplayName());
			if (html) {
				sb.append("</i>");
			}
			sb.append(": ");
			sb.append(value);
			if (html) {
				sb.append("</li>");
			}
			else {
				sb.append('\n');
			}
		}
	}
}
