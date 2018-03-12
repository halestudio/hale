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
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Explanation for join function cells.
 * 
 * @author Kai Schwierczek
 */
public class JoinExplanation extends AbstractCellExplanation implements JoinFunction {

	@Override
	protected String getExplanation(Cell cell, boolean html, ServiceProvider services,
			Locale locale) {
		JoinParameter join = CellUtil.getFirstParameter(cell, PARAMETER_JOIN)
				.as(JoinParameter.class);

		if (join != null && join.getTypes() != null && !join.getTypes().isEmpty()) {

			// types
			StringBuilder types = new StringBuilder();
			boolean first = true;
			for (TypeEntityDefinition type : join.getTypes()) {
				if (first) {
					first = false;
				}
				else {
					types.append(", ");
				}
				types.append(formatEntity(type, html, false, locale));
			}

			StringBuilder sb = new StringBuilder();
			sb.append(MessageFormat.format(getMessage("main", locale), types.toString()));
			sb.append("\n\n");

			// conditions

			for (JoinCondition condition : join.getConditions()) {
				sb.append(formatFullEntity(condition.baseProperty, html, locale));
				sb.append(" = ");
				sb.append(formatFullEntity(condition.joinProperty, html, locale));
				sb.append('\n');
			}
			sb.append('\n');

			// finalize

			String explanation = sb.toString();

			if (html)
				explanation = explanation.replaceAll("\n", "<br />");

			return explanation;
		}

		return null;
	}

	private String formatFullEntity(EntityDefinition ed, boolean html, Locale locale) {
		String result = "";
		while (ed != null) {
			if (!result.isEmpty()) {
				result = "." + result;
			}
			result = formatEntity(ed, html, false, locale) + result;
			ed = AlignmentUtil.getParent(ed);
		}
		return result;
	}
}
