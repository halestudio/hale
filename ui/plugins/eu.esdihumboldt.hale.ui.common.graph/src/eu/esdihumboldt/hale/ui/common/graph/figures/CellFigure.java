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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Priority;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.graph.figures.extension.CellFigureContributionFactory;
import eu.esdihumboldt.hale.ui.common.graph.figures.extension.CellFigureExtension;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * Figure representing a cell.
 * 
 * @author Simon Templer
 * @author Andrea Antonello
 */
public class CellFigure extends CustomShapeFigure {

	private static final ALogger logger = ALoggerFactory.getLogger(CellFigure.class);

	/**
	 * Default constructor
	 * 
	 * @param cell the cell from which to take info from.
	 * @param customFont a custom font to use for the text label, may be
	 *            <code>null</code>
	 * @param isCompatible a boolean to determine the compatibility of the cell
	 *            to the current active mode
	 * @param lastCompatibilityMode name of the last active compatibility mode
	 * @param isInherited whether the cell is an inherited cell
	 */
	public CellFigure(Cell cell, final Font customFont, boolean isCompatible,
			String lastCompatibilityMode, boolean isInherited) {
		super(new StretchedHexagon(10), customFont);

		setAntialias(SWT.ON);

		List<CellFigureContribution> contributors = new ArrayList<>();
		List<CellFigureContributionFactory> factories = CellFigureExtension
				.getCellFigureContributionFactories();
		for (CellFigureContributionFactory factory : factories) {
			try {
				CellFigureContribution contribution = factory.createExtensionObject();
				if (contribution != null) {
					contributors.add(contribution);
				}
			} catch (Exception e) {
				logger.error("Couldn't create task provider", e);
			}

			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = getNumColumns(cell, isCompatible, isInherited, contributors);
			gridLayout.makeColumnsEqualWidth = false;
			gridLayout.marginHeight = 3;
			gridLayout.marginWidth = 3;
			setLayoutManager(gridLayout);

			addLabels(cell, isCompatible, lastCompatibilityMode, isInherited);

			contributors.stream().forEach(c -> c.contribute(this, cell));
		}
	}

	private int getNumColumns(Cell cell, boolean isCompatible, boolean isInherited,
			List<CellFigureContribution> contributors) {
		int numColumns = 1; // main label

		if (!isCompatible) {
			numColumns++;
		}
		if (isInherited) {
			numColumns++;
		}
		if (AlignmentUtil.isTypeCell(cell)) {
			numColumns++;
		}
		if (cell.getPriority() != Priority.NORMAL) {
			numColumns++;
		}

		for (CellFigureContribution contributor : contributors) {
			numColumns += contributor.getLabelColumnCount();
		}

		return numColumns;
	}

	private void addLabels(Cell cell, boolean isCompatible, String lastCompatibilityMode,
			boolean isInherited) {
		if (!isCompatible) {
			Label compatibilityLabel = new Label();
			Image compatibilityImage = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
			GridData compLabelGD = new GridData(GridData.BEGINNING, GridData.FILL, false, true);
			compatibilityLabel.setIcon(compatibilityImage);
			Label toolTipLabel = new Label();
			toolTipLabel
					.setText("Not compatible with " + lastCompatibilityMode + " transformation");
			compatibilityLabel.setToolTip(toolTipLabel);
			add(compatibilityLabel, compLabelGD);
		}

		if (AlignmentUtil.isTypeCell(cell)) {
			// label for displaying the transformation mode
			Image modeImage;
			switch (cell.getTransformationMode()) {
			case active:
				modeImage = CommonSharedImages.getImageRegistry()
						.get(CommonSharedImages.IMG_MARKER_GREEN);
				break;
			case passive:
				modeImage = CommonSharedImages.getImageRegistry()
						.get(CommonSharedImages.IMG_MARKER_YELLOW);
				break;
			case disabled:
			default:
				modeImage = CommonSharedImages.getImageRegistry()
						.get(CommonSharedImages.IMG_MARKER_RED);
			}

			Label modeLabel = new Label(modeImage);
			Label modeToolTip = new Label(cell.getTransformationMode().displayName());
			modeLabel.setToolTip(modeToolTip);

			GridData modeGD = new GridData(GridData.CENTER, GridData.CENTER, false, false);
			add(modeLabel, modeGD);
		}

		Label mainLabel = new Label();
		GridData mainLabelGD = new GridData(GridData.FILL, GridData.FILL, true, true);
		add(mainLabel, mainLabelGD);

		setTextLabel(mainLabel);
		setIconLabel(mainLabel);

		if (isInherited) {
			Label inheritedLabel = new Label(CommonSharedImages.getImageRegistry()
					.get(CommonSharedImages.IMG_INHERITED_ARROW_SMALL));
			Label ineritedToolTip = new Label("Relation by inheritance");
			inheritedLabel.setToolTip(ineritedToolTip);

			GridData inheritedGD = new GridData(GridData.CENTER, GridData.CENTER, false, false);
			add(inheritedLabel, inheritedGD);
		}

		Label priorityLabel = new Label();
		Image priorityImage = null;
		switch (cell.getPriority()) {
		case HIGHEST:
			priorityImage = CommonSharedImages.getImageRegistry()
					.get(CommonSharedImages.IMG_PRIORITY_HIGHEST);
			break;
		case HIGHER:
			priorityImage = CommonSharedImages.getImageRegistry()
					.get(CommonSharedImages.IMG_PRIORITY_HIGHER);
			break;
		case HIGH:
			priorityImage = CommonSharedImages.getImageRegistry()
					.get(CommonSharedImages.IMG_PRIORITY_HIGH);
			break;
		case LOW:
			priorityImage = CommonSharedImages.getImageRegistry()
					.get(CommonSharedImages.IMG_PRIORITY_LOW);
			break;
		case LOWER:
			priorityImage = CommonSharedImages.getImageRegistry()
					.get(CommonSharedImages.IMG_PRIORITY_LOWER);
			break;
		case LOWEST:
			priorityImage = CommonSharedImages.getImageRegistry()
					.get(CommonSharedImages.IMG_PRIORITY_LOWEST);
			break;
		case NORMAL:
		default:
			return;
		}
		priorityLabel.setIcon(priorityImage);
		if (priorityImage != null) {
			Label priorityTip = new Label("Priority: " + cell.getPriority().value());
			priorityLabel.setToolTip(priorityTip);
		}
		GridData priorityLabelGD = new GridData(GridData.CENTER, GridData.FILL, false, true);
		add(priorityLabel, priorityLabelGD);
	}
}
