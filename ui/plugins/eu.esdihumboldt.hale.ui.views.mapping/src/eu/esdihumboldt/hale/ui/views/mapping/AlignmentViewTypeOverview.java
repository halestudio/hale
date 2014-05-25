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

package eu.esdihumboldt.hale.ui.views.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * An Alignment View for Types, overview only.
 * 
 * @author Yasmina Kammeyer
 */
public class AlignmentViewTypeOverview extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping.typeoverview";

	private AlignmentServiceListener alignmentListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		super.createViewControl(parent);

		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);

		// initialize
		if (as.getAlignment() != null) {
			updateViewerInput();
		}

		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						getViewer().setInput(null);
					}
				});
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				// check for removed type cells
				for (Cell cell : cells) {
					if (AlignmentUtil.isTypeCell(cell) == true) {
						updateViewerInput();
						return;
					}
				}
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				Set<? extends Cell> cellAsKeySet = cells.keySet();
				// check for replaced type cells
				for (Cell cell : cellAsKeySet) {
					if (AlignmentUtil.isTypeCell(cell) == true) {
						updateViewerInput();
						return;
					}
				}
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				// check for added type cells
				for (Cell cell : cells) {
					if (AlignmentUtil.isTypeCell(cell) == true) {
						updateViewerInput();
						return;
					}
				}
			}

			@Override
			public void alignmentChanged() {
				updateViewerInput();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				// check for type cells with changed property
				for (Cell cell : cells) {
					if (AlignmentUtil.isTypeCell(cell) == true) {
						updateViewerInput();
						return;
					}
				}
			}

		});

		// listen on SchemaSelections
//		getSite().getWorkbenchWindow().getSelectionService()
//				.addPostSelectionListener(selectionListener = new ISelectionListener() {
//
//					@Override
//					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//
//						if (part != AlignmentViewTypeOverview.this
//								&& selection instanceof IStructuredSelection
//								&& ((IStructuredSelection) selection).getFirstElement() instanceof Cell) {
//							Cell cell = (Cell) ((IStructuredSelection) selection).getFirstElement();
//							if (cell.getSource().values().iterator().next().getDefinition() instanceof TypeEntityDefinition) {
//								setSelectedCell(cell);
//								refreshGraph();
//							}
//						}// end if
//					}
//				});

		// listen on size changes
		getViewer().getControl().addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				updateLayout(true);
			}
		});

	}

	/**
	 * Set the input of the viewer
	 */
	private void updateViewerInput() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				List<Cell> cells = null;

				// get the current alignment
				AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
						AlignmentService.class);
				if (as == null)
					return;
				Alignment alignment = as.getAlignment();
				// cells array
				cells = new ArrayList<Cell>();
				// add current type cells
				for (Cell cell : alignment.getTypeCells()) {
					cells.add(cell);
				}

				getViewer().setInput(cells);
				updateLayout(true);
			}
		});

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#refreshGraph()
	 */
	@Override
	protected void refreshGraph() {

		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				getViewer().refresh();
				updateLayout(true);
			}
		});
	}

	/**
	 * Fill the view toolbar.
	 */
	@Override
	protected void fillToolBar() {
		super.fillToolBar();
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();

		manager.add(new Separator());

	}

	/**
	 * @see WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {

		if (alignmentListener != null) {
			AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
					AlignmentService.class);
			as.removeListener(alignmentListener);
		}
//
//		if (selectionListener != null) {
//			getSite().getWorkbenchWindow().getSelectionService()
//					.removePostSelectionListener(selectionListener);
//		}

		super.dispose();
	}

}
