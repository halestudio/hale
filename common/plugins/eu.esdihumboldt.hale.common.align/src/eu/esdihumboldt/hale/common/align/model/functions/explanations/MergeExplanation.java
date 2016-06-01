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
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for merge function cells.
 * 
 * @author Simon Templer
 */
public class MergeExplanation extends AbstractCellExplanation implements MergeFunction {

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {

		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		List<ParameterValue> properties = (cell.getTransformationParameters() == null) ? (null)
				: (cell.getTransformationParameters().get(PARAMETER_PROPERTY));

		if (source != null && target != null && properties != null && !properties.isEmpty()) {
			StringBuffer propertiesString = new StringBuffer();
			for (int i = 0; i < properties.size(); i++) {
				propertiesString.append(quoteText(properties.get(i).as(String.class), html));

				if (i == properties.size() - 2) {
					propertiesString.append(" and ");
				}
				else if (i < properties.size() - 2) {
					propertiesString.append(", ");
				}
			}

			// XXX additional properties and auto detect of equal properties

			return MessageFormat.format(
					"Merges different instances of the type {0} based on its properties {2} being equal. The values of these properties are merged into one, while the values of the other properties will be available in the target instance of type {1} as separate values for each source instance.",
					formatEntity(source, html, true), formatEntity(target, html, true),
					propertiesString);
		}

		return null;
	}

}
