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
import java.util.List;
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for formatted string cells.
 * 
 * @author Simon Templer
 */
public class FormattedStringExplanation extends AbstractCellExplanation
		implements FormattedStringFunction {

	private static final String EXPLANATION_PATTERN = "Populates the {0} property with a string formatted according to this pattern:\n"
			+ "{1}\nSource property names in curly braces are replaced by the corresponding property value, if the context condition/index matches, otherwise the value isn''t set.";

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String pattern = CellUtil.getFirstParameter(cell, PARAMETER_PATTERN).as(String.class);

		List<? extends Entity> sources = null;
		if (cell.getSource() != null) {
			sources = cell.getSource().get(ENTITY_VARIABLE);
		}

		if (target != null && pattern != null) {
			if (html)
				pattern = "<pre>" + pattern + "</pre>";
			String explanation = MessageFormat.format(EXPLANATION_PATTERN,
					formatEntity(target, html, true), pattern);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			if (sources != null && html) {
				StringBuilder sb = new StringBuilder();
				sb.append("<br /><br />Replacement table:<br />");
				sb.append(
						"<table border=\"1\"><tr><th>Variable name</th><th>Value of the following property</th></tr>");
				for (Entity entity : sources)
					sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>",
							'{' + getEntityNameWithoutCondition(entity) + '}',
							formatEntity(entity, true, false)));
				sb.append("</table>");
				explanation += sb.toString();
			}
			return explanation;
		}

		return null;
	}
}
