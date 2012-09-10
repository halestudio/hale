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

package eu.esdihumboldt.hale.common.align.model.functions.explanations;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for the rename function.
 * 
 * @author Simon Templer
 */
public class RenameExplanation extends AbstractCellExplanation implements RenameFunction {

	/**
	 * @see AbstractCellExplanation#getExplanation(Cell, boolean)
	 */
	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		if (source != null && target != null) {
			String text;
			if (hasIndexCondition(source))
				text = "For the {0} property";
			else
				text = "For each value in {0}";
			text += " adds the same value to the {1} property. If necessary a conversion is applied.";
			String structuralRename = CellUtil.getFirstParameter(cell, PARAMETER_STRUCTURAL_RENAME);
			if (Boolean.parseBoolean(structuralRename))
				text += " Furthermore child properties get added, too, if the property names match.";
			return MessageFormat.format(text, formatEntity(source, html, true),
					formatEntity(target, html, true));
		}

		return null;
	}
}
