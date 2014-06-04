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

package eu.esdihumboldt.hale.ui.function.common;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;
import eu.esdihumboldt.hale.ui.service.cell.TypeCellFocusService;

/**
 * Creates a Control for a Type Cell.
 * 
 * @author Yasmina Kammeyer
 */
public class TypeCellSelector implements ISelectionProvider {

	private Cell selection;

	private final ListenerList selectionChangedListeners = new ListenerList();

	/**
	 * Creates a new selector.
	 * 
	 */
	public TypeCellSelector() {

		selection = null;

	}

	/**
	 * Opens the TypeCellSelectionDialog and get the selection
	 * 
	 * @param shell The shell for the dialog
	 */
	public void performTypeCellDialog(Shell shell) {
		TypeCellSelectionDialog dialog = new TypeCellSelectionDialog(shell.getShell(),
				"Select a type cell", selection);
		if (dialog.open() == TypeCellSelectionDialog.OK) {
			Cell selected = dialog.getObject();
			setSelection(selected);
			fireSelectionChanged();
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return new StructuredSelection(getSelectedCell());
	}

	/**
	 * @return the selected Cell
	 */
	public Cell getSelectedCell() {
		if (selection != null)
			return selection;

		return new DefaultCell(); // empty Cell
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		synchronized (selection) {
			if (selection.isEmpty()) {
				setSelection((Cell) null);
			}
			if (selection instanceof IStructuredSelection)
				if (((IStructuredSelection) selection).getFirstElement() instanceof Cell) {
					Cell cell = (Cell) ((IStructuredSelection) selection).getFirstElement();
					setSelection(cell);
				}// end if
		}
		fireSelectionChanged();
	}

	/**
	 * @param cell The selected Cell
	 */
	private synchronized void setSelection(Cell cell) {
		if (cell != null && AlignmentUtil.isTypeCell(cell)) {
			selection = cell;
		}
		else
			selection = null;
	}

	/**
	 * Fire a selection changed event to all listeners.
	 */
	private void fireSelectionChanged() {

		TypeCellFocusService tc = (TypeCellFocusService) PlatformUI.getWorkbench().getService(
				TypeCellFocusService.class);
		if (isCellSelected())
			tc.setCell(getSelectedCell());
		else
			tc.setCell(null);
	}

	/**
	 * @return true, if selectedCell != null
	 */
	public boolean isCellSelected() {
		return (selection != null);
	}

}
