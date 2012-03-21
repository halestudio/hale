/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.functions.geometric;

import java.text.MessageFormat;

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
	/**
	 * @see eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell, boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		Entity sourceX = cell.getSource().get("x").get(0);
		Entity sourceY = cell.getSource().get("y").get(0);
		Entity sourceZ = null;
		String srsName = CellUtil.getFirstParameter(cell, OrdinatesToPoint.PARAMETER_REFERENCE_SYSTEM);

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
			return MessageFormat.format(message, formatEntity(target, html, true), formatEntity(sourceX, html, true),
					formatEntity(sourceY, html, true), formatEntity(sourceZ, html, true), quoteText(srsName, html));
		}

		return null;
	}
}
