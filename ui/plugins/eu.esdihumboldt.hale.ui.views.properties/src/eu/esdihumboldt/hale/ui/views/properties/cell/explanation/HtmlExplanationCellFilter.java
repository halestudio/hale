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

package eu.esdihumboldt.hale.ui.views.properties.cell.explanation;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellExplanation;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.views.properties.cell.AbstractCellFilter;

/**
 * Filter for cell explanation section.
 * 
 * @author Simon Templer
 */
public class HtmlExplanationCellFilter extends AbstractCellFilter {

	@Override
	public boolean isFiltered(Cell cell) {
		FunctionDefinition<?> function = FunctionUtil.getFunction(
				cell.getTransformationIdentifier(), HaleUI.getServiceProvider());
		if (function != null) {
			CellExplanation explanation = function.getExplanation();
			if (explanation != null) {
				String text = explanation.getExplanationAsHtml(cell, HaleUI.getServiceProvider());
				if (text != null) {
					return false;
				}
				text = explanation.getExplanation(cell, HaleUI.getServiceProvider());
				if (text != null) {
					return false;
				}
			}
		}

		return true;
	}

}
