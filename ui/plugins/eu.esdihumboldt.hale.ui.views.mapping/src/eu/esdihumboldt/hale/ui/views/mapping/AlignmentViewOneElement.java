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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

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

	private TreeLayoutAlgorithm treeLayout;

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
			clear();
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
				clear();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				return; // do nothing
			}

			@Override
			public void alignmentChanged() {
				clear();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				clear();
			}

		});

		// listen on SchemaSelections
		selectionListener = (ISelectionListener) PlatformUI.getWorkbench().getService(
				SchemaSelection.class);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.addPostSelectionListener(selectionListener = new ISelectionListener() {

					@Override
					public void selectionChanged(IWorkbenchPart part, ISelection selection) {
						// do not react on own selected cell
						if (part != AlignmentViewOneElement.this
								&& selection instanceof IStructuredSelection
								&& ((IStructuredSelection) selection).getFirstElement() instanceof Cell) {
							Cell dt = (Cell) ((IStructuredSelection) selection).getFirstElement();

							setContent(dt);
						}
						else {
							return;
						}
					}
				});
		// Create a layout and apply it
		getViewer().setLayoutAlgorithm(createLayout());
	}

	/**
	 * @param cell
	 */
	protected void setContent(Cell cell) {
		// set cell
		if (cell != null) {
			List<Cell> cells = new ArrayList<Cell>();
			cells.add(cell);
			getViewer().setInput(cells);
		}
		updateLayout(true);
	}

	/**
	 * Clears up this View
	 */
	protected void clear() {
		getViewer().setInput(null);
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

	@Override
	protected LayoutAlgorithm createLayout() {
		treeLayout = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.BOTTOM_UP);
		return treeLayout;
	}

	/**
	 * Update the layout to the view size.
	 * 
	 * @param triggerLayout if the layout should be applied directly
	 */
	@Override
	protected void updateLayout(boolean triggerLayout) {
		int width = getViewer().getControl().getSize().x;
		int hight = getViewer().getControl().getSize().y;

		treeLayout.setNodeSpace(new Dimension((int) (width * 0.9), (hight - 1) / 3));

		if (triggerLayout) {
			getViewer().applyLayout();
		}
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
