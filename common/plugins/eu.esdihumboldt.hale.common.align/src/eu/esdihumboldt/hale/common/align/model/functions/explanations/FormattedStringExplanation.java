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
import java.util.Map;
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.FormattedStringFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Explanation for formatted string cells.
 * 
 * @author Simon Templer
 */
public class FormattedStringExplanation extends AbstractCellExplanation
		implements FormattedStringFunction {

	@Override
	protected String getExplanation(Cell cell, boolean html, ServiceProvider services,
			Locale locale) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String pattern = CellUtil.getFirstParameter(cell, PARAMETER_PATTERN).as(String.class);

		List<? extends Entity> sources = null;
		if (cell.getSource() != null) {
			sources = cell.getSource().get(ENTITY_VARIABLE);
		}

		if (target != null && pattern != null) {
			if (html)
				pattern = "<pre>" + pattern + "</pre>";
			String explanation = MessageFormat.format(getMessage("main", locale),
					formatEntity(target, html, true, locale), pattern);
			if (html)
				explanation = explanation.replaceAll("\n", "<br />");
			if (sources != null && html) {
				Map<String, String> varToProperty = sources.stream()
						.collect(Collectors.toMap(entity -> {
							return '{' + getEntityNameWithoutCondition(entity) + '}';
						} , entity -> {
							return formatEntity(entity, true, false, locale);
						}));
				explanation += buildReplacementTable(varToProperty, locale);
			}
			return explanation;
		}

		return null;
	}
}
