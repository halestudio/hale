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

package eu.esdihumboldt.hale.ui.common.graph.figures;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;

/**
 * A Figure that extends the EntityFigure
 * 
 * @author Yasmina Kammeyer
 */
public class EntityFigureWithData extends CustomShapeFigure {

	/**
	 * @param painter the painter for the shape, e.g. FingerPost
	 * @param customFont the font used for text
	 */
	public EntityFigureWithData(ShapePainter painter, Font customFont) {
		super(painter, customFont);
	}

	/**
	 * @param painter the painter for the shape, e.g. FingerPost
	 */
	public EntityFigureWithData(ShapePainter painter) {
		super(painter);
	}

	/**
	 * Create a entity figure with additional text.
	 * 
	 * @param painter the shape
	 * @param contextText the context text, may be <code>null</code>
	 * @param additionalText the additional text, may be <code>null</code>
	 * @param customFont a custom font to use, may be <code>null</code>
	 */
	public EntityFigureWithData(ShapePainter painter, final String contextText,
			final String additionalText, final Font customFont) {
		super(painter, customFont);
		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		// extend the number of columns
		if (contextText != null)
			gridLayout.numColumns++;

		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		gridLayout.makeColumnsEqualWidth = true;

		setLayoutManager(gridLayout);

		// Create Label Provider
		Label label = new Label();
		GridData gridData = new GridData(GridData.BEGINNING, GridData.FILL, true, true, 1, 3);
		add(label, gridData);

		// create Label for context text
		if (contextText != null) {
			Label contextLabel = new Label();
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			// XXX uses the same color as in schema explorer label provider -
			// centralize this?
			contextLabel.setForegroundColor(colorRegistry.get(JFacePreferences.DECORATIONS_COLOR));
			if (customFont != null) {
				contextLabel.setFont(customFont);
			}
			contextLabel.setText(contextText);
			contextLabel.setToolTip(new Label(contextText));
			GridData contextGridData = new GridData(GridData.END, GridData.CENTER, true, true);
			add(contextLabel, contextGridData);
		}

		// create Label for additional text
		if (additionalText != null) {
			Label addTextLabel = new Label();
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			// XXX uses the same color as in schema explorer label provider -
			// centralize this?
			addTextLabel.setForegroundColor(colorRegistry.get(JFacePreferences.COUNTER_COLOR));
			if (customFont != null) {
				addTextLabel.setFont(customFont);
			}
			addTextLabel.setText(additionalText);
			GridData cardGridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, true);
			add(addTextLabel, cardGridData);
		}

		setTextLabel(label);
		setIconLabel(label);

	}

}
