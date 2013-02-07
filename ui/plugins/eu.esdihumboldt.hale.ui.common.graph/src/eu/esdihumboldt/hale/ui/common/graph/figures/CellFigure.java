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

package eu.esdihumboldt.hale.ui.common.graph.figures;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * Figure representing a cell.
 * 
 * @author Simon Templer
 * @author Andrea Antonello
 */
public class CellFigure extends CustomShapeFigure {

	private final String originalCellId;

	/**
	 * Default constructor
	 * 
	 * @param cell the cell from which to take info from.
	 */
	public CellFigure(Cell cell) {
		super(new StretchedHexagon(10));

		originalCellId = cell.getId();
		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);

		addLabels(cell);

	}

	private void addLabels(Cell cell) {
		Label mainLabel = new Label();
		GridData mainLabelGD = new GridData(GridData.FILL, GridData.FILL, true, true);
		add(mainLabel, mainLabelGD);

		setTextLabel(mainLabel);
		setIconLabel(mainLabel);

		Label priorityLabel = new Label();
		Image priorityImage = null;
		switch (cell.getPriority()) {
		case HIGH:
			priorityImage = CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_PRIORITY_HIGH);
			break;
		case LOW:
			priorityImage = CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_PRIORITY_LOW);
			break;
		case NORMAL:
		default:
			priorityImage = CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_PRIORITY_NORMAL);
			break;
		}
		priorityLabel.setIcon(priorityImage);
		GridData priorityLabelGD = new GridData(GridData.CENTER, GridData.FILL, false, true);
		add(priorityLabel, priorityLabelGD);
	}

//	/**
//	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsPropertyChanged(java.lang.Iterable,
//	 *      java.lang.String)
//	 */
//	@Override
//	public void cellsPropertyChanged(Iterable<Cell> cells, String propertyName) {
//		Iterator<Cell> cellIterator = cells.iterator();
//		if (cellIterator.hasNext()) {
//			Cell cell = cellIterator.next();
//			if (cell.getId().equals(originalCellId)) {
//				removeAll();
//				addLabels(cell);
//			}
//		}
//	}
//
//	/**
//	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#alignmentCleared()
//	 */
//	@Override
//	public void alignmentCleared() {
//		// unused
//	}
//
//	/**
//	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsAdded(java.lang.Iterable)
//	 */
//	@Override
//	public void cellsAdded(Iterable<Cell> cells) {
//		// unused
//	}
//
//	/**
//	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellReplaced(eu.esdihumboldt.hale.common.align.model.Cell,
//	 *      eu.esdihumboldt.hale.common.align.model.Cell)
//	 */
//	@Override
//	public void cellReplaced(Cell oldCell, Cell newCell) {
//		// unused
//	}
//
//	/**
//	 * @see eu.esdihumboldt.hale.ui.service.align.AlignmentServiceListener#cellsRemoved(java.lang.Iterable)
//	 */
//	@Override
//	public void cellsRemoved(Iterable<Cell> cells) {
//		// unused
//	}

}
