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
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;

/**
 * Explanation for OrdinatesToPoint cells.
 * 
 * @author Kai Schwierczek
 */
public class OrdinatesToPointExplanation implements CellExplanation {
	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanation(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanation(Cell cell) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());
		Entity sourceX = cell.getSource().get("x").get(0);
		Entity sourceY = cell.getSource().get("y").get(0);
		Entity sourceZ = null;
		String srsName = CellUtil.getFirstParameter(cell, OrdinatesToPoint.PARAMETER_REFERENCE_SYSTEM);

		if (!cell.getSource().get("z").isEmpty())
			sourceZ = cell.getSource().get("z").get(0);

		if (target != null && sourceX != null && sourceY != null) {
			String message = "Fills the \"{0}\" property with geometry points with the coordinates ({1} {2}";
			message += (sourceZ == null ? "" : " {3}") + ").";
			if (srsName != null)
				message += " The reference system \"{4}\" is used.";
			return MessageFormat.format(
					message, target.getDefinition().getDefinition().getDisplayName(),
					sourceX.getDefinition().getDefinition().getDisplayName(),
					sourceY.getDefinition().getDefinition().getDisplayName(),
					sourceZ == null ? null : sourceZ.getDefinition().getDefinition().getDisplayName(),
					srsName);
		}

		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.model.CellExplanation#getExplanationAsHtml(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String getExplanationAsHtml(Cell cell) {
		return null;
	}
}
