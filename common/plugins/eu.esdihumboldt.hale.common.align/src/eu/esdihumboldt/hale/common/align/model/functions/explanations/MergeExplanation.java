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
import java.util.stream.Collectors;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Explanation for merge function cells.
 * 
 * @author Simon Templer
 */
public class MergeExplanation extends AbstractCellExplanation implements MergeFunction {

	@Override
	protected String getExplanation(Cell cell, boolean html, ServiceProvider services,
			Locale locale) {

		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		List<ParameterValue> properties = (cell.getTransformationParameters() == null) ? (null)
				: (cell.getTransformationParameters().get(PARAMETER_PROPERTY));

		if (source != null && target != null) {
			if (properties != null && !properties.isEmpty()) {
				List<String> props = properties.stream()
						.map(prop -> quoteText(prop.as(String.class), html))
						.collect(Collectors.toList());

				String propertiesString = enumerateJoin(props, locale);

				// XXX additional properties and auto detect of equal properties

				return MessageFormat.format(getMessage("main", locale),
						formatEntity(source, html, true, locale),
						formatEntity(target, html, true, locale), propertiesString);
			}
			else {
				return MessageFormat.format(getMessage("all", locale),
						formatEntity(source, html, true, locale),
						formatEntity(target, html, true, locale));
			}
		}

		return null;
	}

}
