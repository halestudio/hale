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

package eu.esdihumboldt.cst.functions.string;

import java.text.MessageFormat;
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for date extraction cells.
 * 
 * @author Kai Schwierczek
 */
public class DateExtractionExplanation extends AbstractCellExplanation {

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		String format = CellUtil.getFirstParameter(cell, DateExtraction.PARAMETER_DATE_FORMAT)
				.as(String.class);

		if (target != null && format != null) {
			return MessageFormat.format(
					"Populates the {1} property with a date created by parsing the {0} property using the format {2}.",
					formatEntity(source, html, true), formatEntity(target, html, true),
					quoteText(format, html));
		}

		return null;
	}
}
