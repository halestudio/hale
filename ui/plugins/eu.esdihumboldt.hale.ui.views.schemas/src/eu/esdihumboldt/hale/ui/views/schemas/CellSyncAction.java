/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.views.schemas;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.views.schemas.explorer.SchemaExplorer;

/**
 * Action that allows syncing the schemas view with a cell selection.
 * 
 * TODO adapt to also react on entity definition or schema element selections?
 * 
 * @author Simon Templer
 */
public class CellSyncAction extends Action implements ISelectionListener {

	private final ISelectionService selectionService;
	private final SchemaExplorer sourceExplorer;
	private final SchemaExplorer targetExplorer;

	/**
	 * Create a cell sync action. The action must be {@link #dispose()}d.
	 * 
	 * @param selectionService the selection service
	 * @param targetExplorer the target schema explorer
	 * @param sourceExplorer the source schema explorer
	 * 
	 */
	public CellSyncAction(ISelectionService selectionService, SchemaExplorer sourceExplorer,
			SchemaExplorer targetExplorer) {
		super(null, AS_CHECK_BOX);

		this.selectionService = selectionService;
		this.sourceExplorer = sourceExplorer;
		this.targetExplorer = targetExplorer;

		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));

		setDescription("Synchronise with cell selection");
		setToolTipText(getDescription());

		selectionService.addPostSelectionListener(this);
	}

	@Override
	public void run() {
		if (isChecked()) {
			selectionChanged(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getSelection());
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (isChecked() && selection instanceof IStructuredSelection && !selection.isEmpty()
				&& ((IStructuredSelection) selection).getFirstElement() instanceof Cell) {
			Cell cell = (Cell) ((IStructuredSelection) selection).getFirstElement();

			if (cell.getSource() != null) {
				sourceExplorer.getTreeViewer().setSelection(
						createSelection(cell.getSource().values()), true);
			}
			if (cell.getTarget() != null) {
				targetExplorer.getTreeViewer().setSelection(
						createSelection(cell.getTarget().values()), true);
			}
		}
	}

	private ISelection createSelection(Collection<? extends Entity> entities) {
		Set<EntityDefinition> defs = new HashSet<EntityDefinition>();

		for (Entity entity : entities) {
			defs.add(entity.getDefinition());
		}

		return new StructuredSelection(defs.toArray());
	}

	/**
	 * Dispose the action, removing any service listeners.
	 */
	public void dispose() {
		selectionService.removePostSelectionListener(this);
	}

}
