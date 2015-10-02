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

package eu.esdihumboldt.cst.functions.groovy;

import java.text.MessageFormat;
import java.util.List;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.io.Text;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Explanation for groovy cells.
 * 
 * @author Kai Schwierczek
 */
public class GroovyExplanation extends AbstractCellExplanation implements GroovyConstants {

	private static final String EXPLANATION_PATTERN = "Populates the {0} property with the result of the following groovy script:\n\n"
			+ "{1}\n\nSource property names are bound to the corresponding value, if the context condition/index matches, otherwise the value isn't set.";

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		Value scriptValue = CellUtil.getFirstParameter(cell, PARAMETER_SCRIPT);
		String script;
		// try retrieving as text
		Text text = scriptValue.as(Text.class);
		if (text != null) {
			script = text.getText();
		}
		else {
			// fall back to string value
			script = scriptValue.as(String.class);
		}

		List<? extends Entity> sources = (cell.getSource() == null) ? (null) : (cell.getSource()
				.get(ENTITY_VARIABLE));

		if (target != null && script != null) {
			if (html)
				script = "<pre>" + script + "</pre>";
			String explanation = MessageFormat.format(EXPLANATION_PATTERN,
					formatEntity(target, html, true), script);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			if (html && sources != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("<br /><br />Replacement table:<br />");
				sb.append("<table border=\"1\"><tr><th>Variable name</th><th>Value of the following property</th></tr>");
				for (Entity entity : sources)
					sb.append(String.format("<tr><td>%s</td><td>%s</td></tr>",
							getEntityNameWithoutCondition(entity).replace('.', '_'),
							formatEntity(entity, true, false)));
				sb.append("</table>");
				explanation += sb.toString();
			}
			return explanation;
		}

		return null;
	}
}
