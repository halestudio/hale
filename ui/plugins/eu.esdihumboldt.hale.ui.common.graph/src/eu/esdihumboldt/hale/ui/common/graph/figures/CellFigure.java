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
import org.eclipse.swt.graphics.Font;
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

	/**
	 * Default constructor
	 * 
	 * @param cell the cell from which to take info from.
	 * @param customFont a custom font to use for the text label, may be
	 *            <code>null</code>
	 */
	public CellFigure(Cell cell, final Font customFont) {
		super(new StretchedHexagon(10), customFont);

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
		case LOWEST:
			priorityImage = CommonSharedImages.getImageRegistry().get(
					CommonSharedImages.IMG_PRIORITY_LOWEST);
			break;
		case NORMAL:
		default:
			return;
		}
		priorityLabel.setIcon(priorityImage);
		GridData priorityLabelGD = new GridData(GridData.CENTER, GridData.FILL, false, true);
		add(priorityLabel, priorityLabelGD);
	}

}
