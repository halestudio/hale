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

package eu.esdihumboldt.hale.ui.service.align.internal.tester;

import org.eclipse.core.expressions.PropertyTester;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Tests on {@link Cell}s based on the {@link AlignmentService}.
 * 
 * @author Simon Templer
 */
public class CellTester extends PropertyTester {

	/**
	 * The property namespace for this tester.
	 */
	public static final String NAMESPACE = "eu.esdihumboldt.hale.ui.service.align.cell";

	/**
	 * The property that specifies if a cell may be removed.
	 */
	public static final String PROPERTY_CELL_ALLOW_REMOVE = "allow_remove";

	/**
	 * The property that specifies if a cell may be edited.
	 */
	public static final String PROPERTY_CELL_ALLOW_EDIT = "allow_edit";

	/**
	 * The property that specifies if a cell is a type cell.
	 */
	public static final String PROPERTY_CELL_IS_TYPE_CELL = "type_cell";

	/**
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object,
	 *      java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver == null) {
			return false;
		}

		if (property.equals(PROPERTY_CELL_ALLOW_REMOVE) && receiver instanceof Cell) {
			return testAllowRemove((Cell) receiver);
		}

		if (property.equals(PROPERTY_CELL_ALLOW_EDIT) && receiver instanceof Cell) {
			return !((Cell) receiver).isBaseCell();
		}

		if (property.equals(PROPERTY_CELL_IS_TYPE_CELL) && receiver instanceof Cell) {
			return AlignmentUtil.isTypeCell((Cell) receiver);
		}

		return false;
	}

	/**
	 * Test if removing the given cell is allowed.
	 * 
	 * @param cell the cell to remove
	 * @return if removing the cell is allowed
	 */
	private boolean testAllowRemove(Cell cell) {
		if (cell.isBaseCell()) {
			// never allow removing base alignment cells
			return false;
		}
		// always allow removing type & property cells
		return true;
	}

}
