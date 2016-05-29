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

package eu.esdihumboldt.hale.ui.service.align.internal.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;

/**
 * Removes selected cells from the alignment
 * 
 * @author Simon Templer
 */
public class RemoveCellHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// collect cells from selection
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Delete cells",
				"Do you really want to delete the selected cells?")) {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);

			if (selection instanceof IStructuredSelection) {
				List<?> list = ((IStructuredSelection) selection).toList();
				List<Cell> cells = new ArrayList<Cell>();
				for (Object object : list) {
					if (object instanceof Cell) {
						// FIXME sanity checks for cell deletion? (e.g. don't
						// allow remove type mapping if there are properties
						// mapped?) where to do it?
						// For now only done in activeWhen defined for handler
						cells.add((Cell) object);
					}
				}
				as.removeCells(cells.toArray(new Cell[cells.size()]));
			}
		}

		return null;
	}

}
