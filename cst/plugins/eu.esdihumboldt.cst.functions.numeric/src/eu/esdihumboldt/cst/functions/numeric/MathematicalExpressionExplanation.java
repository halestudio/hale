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

package eu.esdihumboldt.cst.functions.numeric;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for mathematical expression cells.
 * 
 * @author Kai Schwierczek
 */
public class MathematicalExpressionExplanation extends AbstractCellExplanation {

	private static final String EXPLANATION_PATTERN = "Populates the {0} property with a number resulting from the following calculation:\n"
			+ "{1}\nSource property names are replaced by the corresponding property value, if the context condition/index matches, otherwise the value isn't set.";

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String expression = CellUtil
				.getFirstParameter(cell, MathematicalExpression.PARAMETER_EXPRESSION)
				.as(String.class);
		List<? extends Entity> sources = cell.getSource()
				.get(MathematicalExpression.ENTITY_VARIABLE);

		if (target != null && expression != null) {
			if (html)
				expression = "<pre>" + expression + "</pre>";
			String explanation = MessageFormat.format(EXPLANATION_PATTERN,
					formatEntity(target, html, true), expression);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			if (html) {
				StringBuilder sb = new StringBuilder();
				sb.append("<br /><br />Replacement table:<br />");
				sb.append(
						"<table border=\"1\"><tr><th>Variable name</th><th>Value of the following property</th></tr>");
				for (Entity entity : sources)
					sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>",
							getEntityNameWithoutCondition(entity),
							formatEntity(entity, true, false)));
				sb.append("</table>");
				explanation += sb.toString();
			}
			return explanation;
		}

		return null;
	}
}
