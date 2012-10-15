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
			String structuralRename = CellUtil.getFirstRawParameter(cell,
					PARAMETER_STRUCTURAL_RENAME);
			if (Boolean.parseBoolean(structuralRename))
				text += " Furthermore child properties get added, too, if the property names match.";
			return MessageFormat.format(text, formatEntity(source, html, true),
					formatEntity(target, html, true));
		}

		return null;
	}
}
