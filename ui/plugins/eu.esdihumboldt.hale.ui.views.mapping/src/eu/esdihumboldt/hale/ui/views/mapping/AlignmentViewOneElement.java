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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.graph.labels.CustomGraphLabelProvider;
import eu.esdihumboldt.hale.ui.selection.SchemaSelection;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * Alignment View for one element and additional data.
 * 
 * @author Yasmina Kammeyer
 */
public class AlignmentViewOneElement extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping.alignmentoneelement";

	private AlignmentServiceListener alignmentListener;

	private ISelectionListener selectionListener;

	/**
	 * @see eu.esdihumboldt.hale.ui.views.mapping.AbstractMappingView#createViewControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createViewControl(Composite parent) {
		super.createViewControl(parent);

		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);

		// update the view on last selected relation
		if (as.getAlignment() != null) {
			update();
		}

		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				clear();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				clear();
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
//				if (cells.containsKey(getViewer().getInput())) {
//					update(cells.get(getViewer().getInput()));
//				}
				update();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				return; // do nothing
			}

			@Override
			public void alignmentChanged() {
				update();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				update();
			}

		});

		// listen on SchemaSelections
		selectionListener = (ISelectionListener) PlatformUI.getWorkbench().getService(
				SchemaSelection.class);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(selectionListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {

						if (selection instanceof IStructuredSelection
								&& ((IStructuredSelection) selection).getFirstElement() instanceof Cell) {
							Cell dt = (Cell) ((IStructuredSelection) selection).getFirstElement();

							setContent(dt);
						}
						else {
							return;
						}
					}
				});
	}

	/**
	 * @param dt
	 */
	protected void setContent(Cell cell) {
		// set cells
		if (cell != null) {
			List<Cell> cells = new ArrayList<Cell>();
			cells.add(cell);
			getViewer().setInput(cells);
		}
	}

	/**
	 * Clears up this View
	 */
	protected void clear() {
		getViewer().setInput(null);
	}

	/**
	 * Update the view on last selected relation
	 * 
	 * @param selection the selection
	 */
	protected void update() {
		getViewer().refresh();
	}

	/**
	 * Create the label provider to be used for the graph
	 * 
	 * @return the label provider
	 */
	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new CustomGraphLabelProvider(HaleUI.getServiceProvider());
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

		if (selectionListener != null) {
			getSite().getWorkbenchWindow().getSelectionService()
					.removePostSelectionListener(selectionListener);
		}

		super.dispose();
	}
}
