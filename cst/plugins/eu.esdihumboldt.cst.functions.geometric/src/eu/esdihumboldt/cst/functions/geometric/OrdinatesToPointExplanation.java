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
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for OrdinatesToPoint cells.
 * 
 * @author Kai Schwierczek
 */
public class OrdinatesToPointExplanation extends AbstractCellExplanation {

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		Entity sourceX = cell.getSource().get("x").get(0);
		Entity sourceY = cell.getSource().get("y").get(0);
		Entity sourceZ = null;

		String srsName = CellUtil
				.getFirstParameter(cell, OrdinatesToPoint.PARAMETER_REFERENCE_SYSTEM)
				.as(String.class);

		if (!cell.getSource().get("z").isEmpty())
			sourceZ = cell.getSource().get("z").get(0);

		if (target != null && sourceX != null && sourceY != null) {
			String message = "Fills the {0} property with ";
			if (hasIndexCondition(target))
				message += "a geometry point";
			else
				message += "geometry points";
			message += " where the {1} property is x, the {2} property is y";
			if (sourceZ != null)
				message += ", the {3} property is z";
			message += ".";
			if (srsName != null)
				message += " The reference system {4} is used.";
			return MessageFormat.format(message, formatEntity(target, html, true),
					formatEntity(sourceX, html, true), formatEntity(sourceY, html, true),
					formatEntity(sourceZ, html, true), quoteText(srsName, html));
		}

		return null;
	}
}
