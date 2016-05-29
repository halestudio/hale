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

package eu.esdihumboldt.hale.ui.function.contribution;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.TransformationMode;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.util.selection.SelectionTrackerUtil;

/**
 * Contribution item offering possibilities to set a cell's transformation mode.
 * 
 * @author Simon Templer
 */
public class SetTransformationModeContribution extends ContributionItem {

	/**
	 * Sets a transformation mode on a specific cell.
	 */
	public class SetModeAction extends Action {

		private final TransformationMode mode;
		private final String cellId;

		/**
		 * Create an action to set a transformation mode on a cell.
		 * 
		 * @param mode the transformation mode
		 * @param cellId the cell identifier
		 */
		public SetModeAction(TransformationMode mode, String cellId) {
			this.mode = mode;
			this.cellId = cellId;

			setText(mode.displayName());

			/*
			 * XXX Image for transformation mode now handled both here and in
			 * CellFigure. Should be handled in a single place.
			 */
			ImageDescriptor desc = null;
			switch (mode) {
			case active:
				desc = CommonSharedImages.getImageRegistry()
						.getDescriptor(CommonSharedImages.IMG_MARKER_GREEN);
				break;
			case passive:
				desc = CommonSharedImages.getImageRegistry()
						.getDescriptor(CommonSharedImages.IMG_MARKER_YELLOW);
				break;
			case disabled:
				desc = CommonSharedImages.getImageRegistry()
						.getDescriptor(CommonSharedImages.IMG_MARKER_RED);
			}
			if (desc != null) {
				setImageDescriptor(desc);
			}
		}

		@Override
		public void run() {
			AlignmentService as = PlatformUI.getWorkbench().getService(AlignmentService.class);
			as.setCellProperty(cellId, Cell.PROPERTY_TRANSFORMATION_MODE, mode);
		}

	}

	/**
	 * @see AbstractFunctionWizardContribution#fill(Menu, int)
	 */
	@Override
	public void fill(Menu menu, int index) {
		if (getOriginalCell() == null)
			return;

		Cell cell = getOriginalCell();
		TransformationMode currentmode = cell.getTransformationMode();
		for (TransformationMode mode : TransformationMode.values()) {
			if (mode != currentmode) {
				IAction action = new SetModeAction(mode, cell.getId());
				IContributionItem item = new ActionContributionItem(action);
				item.fill(menu, index++);
			}
		}
	}

	/**
	 * Get the cell
	 * 
	 * @return the cell
	 */
	public Cell getOriginalCell() {
		// retrieve first selected cell
		IStructuredSelection sel = SelectionTrackerUtil.getTracker()
				.getSelection(IStructuredSelection.class);
		for (Object object : sel.toList()) {
			if (object instanceof Cell) {
				return (Cell) object;
			}
		}
		return null;
	}

}
