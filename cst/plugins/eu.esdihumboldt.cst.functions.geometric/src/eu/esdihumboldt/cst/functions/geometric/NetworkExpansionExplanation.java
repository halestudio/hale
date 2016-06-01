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

package eu.esdihumboldt.cst.functions.geometric;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for {@link NetworkExpansion} cells.
 * 
 * @author Simon Templer
 * @author Kai Schwierczek
 */
public class NetworkExpansionExplanation extends AbstractCellExplanation
		implements NetworkExpansionFunction {

	private static final String EXPLANATION_PATTERN = "Takes a geometry found in the {0} property and creates a buffer geometry. The buffer geometry is assigned to the {1} property in the target type.\n"
			+ "The following expression specifies the buffer size used:\n" + "{2}\n"
			+ "Source property variables in the expression are replaced by the corresponding property value, if the context condition/index matches, otherwise the value isn't set.";

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String expression = CellUtil.getFirstParameter(cell, PARAMETER_BUFFER_WIDTH)
				.as(String.class);
		List<? extends Entity> variables = cell.getSource().get(ENTITY_VARIABLE);
		List<? extends Entity> geom = cell.getSource().get(null);

		if (target != null && expression != null) {
			if (html)
				expression = "<pre>" + expression + "</pre>";
			String explanation = MessageFormat.format(EXPLANATION_PATTERN,
					formatEntity(geom.get(0), html, true), formatEntity(target, html, true),
					expression);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			if (html) {
				StringBuilder sb = new StringBuilder(); // TODO unify the
														// replacement tables by
														// introducing a common
														// method to produce
														// them
				sb.append("<br /><br />Replacement table:<br />");
				sb.append(
						"<table border=\"1\"><tr><th>Variable name</th><th>Value of the following property</th></tr>");
				for (Entity entity : variables)
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
