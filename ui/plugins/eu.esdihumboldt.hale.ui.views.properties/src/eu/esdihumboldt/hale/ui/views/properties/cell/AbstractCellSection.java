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

package eu.esdihumboldt.hale.ui.views.properties.cell;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;
import eu.esdihumboldt.hale.ui.views.properties.AbstractSingleObjectSection;

/**
 * Cell section base class.
 * 
 * @author Simon Templer
 */
public abstract class AbstractCellSection extends AbstractSingleObjectSection {

	private Cell cell;

	/**
	 * @see AbstractSingleObjectSection#setInput(Object)
	 */
	@Override
	protected void setInput(Object input) {
		input = TransformationTreeUtil.extractObject(input);

		if (input instanceof Cell) {
			setCell((Cell) input);
		}
	}

	/**
	 * Set the input cell.
	 * 
	 * @param input the cell
	 */
	private void setCell(Cell input) {
		this.cell = input;
	}

	/**
	 * Get the current cell.
	 * 
	 * @return the cell
	 */
	public Cell getCell() {
		return cell;
	}

}
