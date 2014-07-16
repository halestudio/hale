/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.cell.internal;

import java.util.concurrent.CopyOnWriteArraySet;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusListener;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService;

/**
 * Implementation of TypeCellFocusService
 * 
 * @author Yasmina Kammeyer
 */
public class TypeCellFocusServiceImpl implements TypeCellFocusService {

	private final CopyOnWriteArraySet<TypeCellFocusListener> listeners = new CopyOnWriteArraySet<TypeCellFocusListener>();

	private Cell lastSelectedCell;

	/**
	 * @see eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService#setCell(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public void setCell(Cell cell) {
		if (cell == null || AlignmentUtil.isTypeCell(cell)) {
			lastSelectedCell = cell;
			notifyCellChanged(cell);
		}

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService#getLastSelectedTypeCell()
	 */
	@Override
	public Cell getLastSelectedTypeCell() {
		return lastSelectedCell;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService#addListener(eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusListener)
	 */
	@Override
	public void addListener(TypeCellFocusListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService#removeListener(eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusListener)
	 */
	@Override
	public void removeListener(TypeCellFocusListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @param cell The cell that will be pushed to listeners.
	 * 
	 */
	protected void notifyCellChanged(Cell cell) {
		for (TypeCellFocusListener listener : listeners) {
			listener.dataChanged(cell);
		}
	}

}
