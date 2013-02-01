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
import org.eclipse.ui.PlatformUI;

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

		return false;
	}

	/**
	 * Test if removing the given cell is allowed.
	 * 
	 * @param cell the cell to remove
	 * @return if removing the cell is allowed
	 */
	private boolean testAllowRemove(Cell cell) {
		if (!AlignmentUtil.isTypeCell(cell)) {
			// always allow removing property cells
			return true;
		}

		// only allow removing a type cell if there are no property cells
		// associated
		/*
		 * FIXME must we test if there are other type cells that are related to
		 * the cell? XXX but even then we would not be safe to remove the type
		 * cell, as this might be in the context of the removal of multiple type
		 * cells, removing both might leave us again in an invalid state. XXX
		 * solutions could be: only allow deleting one cell at a time and not
		 * multiple or performing the check on all cells to be deleted.
		 */
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);
		return as.getAlignment().getPropertyCells(cell).isEmpty();
	}

}
