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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.service.align.AlignmentService;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceAdapter;
import eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener;

/**
 * An Alignment View for Types, overview and navigation.
 * 
 * @author Yasmina Kammeyer
 */
public class AlignmentViewTypesOnly extends AbstractMappingView {

	/**
	 * The view ID
	 */
	public static final String ID = "eu.esdihumboldt.hale.ui.views.mapping.alignmenttypes";

	private AlignmentServiceListener alignmentListener;

	private TreeLayoutAlgorithm treeLayout;

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
			refresh();
		}

		as.addListener(alignmentListener = new AlignmentServiceAdapter() {

			@Override
			public void alignmentCleared() {
				refresh();
			}

			@Override
			public void cellsRemoved(Iterable<Cell> cells) {
				refresh();
			}

			@Override
			public void cellsReplaced(Map<? extends Cell, ? extends Cell> cells) {
				refresh();
			}

			@Override
			public void cellsAdded(Iterable<Cell> cells) {
				refresh();
			}

			@Override
			public void alignmentChanged() {
				refresh();
			}

			@Override
			public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
				refresh();
			}

		});

	}

	/**
	 * Refresh the input of the viewer. Set all (Type) cells from the Alignment.
	 */
	protected void refresh() {
		List<Cell> cells = null;

		// get the current alignment
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(
				AlignmentService.class);
		Alignment alignment = as.getAlignment();
		// cells array
		cells = new ArrayList<Cell>();
		// add current type cells
		for (Cell cell : alignment.getTypeCells()) {
			cells.add(cell);
		}

		getViewer().setInput(cells);
		updateLayout();
	}

	/**
	 * Update the view
	 * 
	 */
	protected void update(Iterable<Cell> cells) {

		List<Cell> nCells = null;

//		if (getViewer().getInput() == null) {
//			refresh();
//			return;
//		}
//		
//		else {
//			if (getViewer().getInput() instanceof List<?>) {
//				List<?> currentCells = (List<?>) getViewer().getInput();
//				for(Object cell: currentCells)
//					if(cell instanceof Cell)
//				cells = (List<Cell>) getViewer().getInput();
//			}
//		}

		getViewer().setInput(nCells);
		updateLayout();
	}

	@Override
	protected LayoutAlgorithm createLayout() {
		treeLayout = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.RIGHT_LEFT);
		return treeLayout;
	}

	/**
	 * used to update the Layout
	 * 
	 * @see AlignmentView
	 */
	private void updateLayout() {
		int width = getViewer().getControl().getSize().x;

		treeLayout.setNodeSpace(new Dimension((width - 10) / 3, 30));

		getViewer().applyLayout();
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

		super.dispose();
	}

}
