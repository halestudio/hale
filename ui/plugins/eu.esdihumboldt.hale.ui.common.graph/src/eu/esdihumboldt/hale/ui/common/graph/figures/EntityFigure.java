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
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;

import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;

/**
 * Figure for entities displaying an optional context text additional to the
 * entity name and icon.
 * 
 * @author Simon Templer
 */
public class EntityFigure extends CustomShapeFigure {

	/**
	 * Create a entity figure.
	 * 
	 * @param painter the shape
	 * @param contextText the context text, may be <code>null</code>
	 * @param cardinalityText the cardinality text, may be <code>null</code>
	 */
	public EntityFigure(ShapePainter painter, final String contextText, final String cardinalityText) {
		super(painter);

		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		if (cardinalityText != null)
			gridLayout.numColumns++;
		if (contextText != null)
			gridLayout.numColumns++;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);

		// the label for the label provider text and image
		Label label = new EndSubTextLabel();
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		add(label, gridData);

		// the additional label for the cardinality text
		if (cardinalityText != null) {
			Label cardLabel = new Label();
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			// XXX uses the same color as in schema explorer label provider -
			// centralize this?
			cardLabel.setForegroundColor(colorRegistry.get(JFacePreferences.COUNTER_COLOR));
			cardLabel.setText(cardinalityText);
			GridData cardGridData = new GridData(GridData.END, GridData.CENTER, false, true);
			add(cardLabel, cardGridData);
		}

		// the additional label for the context text
		if (contextText != null) {
			Label contextLabel = new Label();
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			// XXX uses the same color as in schema explorer label provider -
			// centralize this?
			contextLabel.setForegroundColor(colorRegistry.get(JFacePreferences.DECORATIONS_COLOR));
			contextLabel.setText(contextText);
			GridData contextGridData = new GridData(GridData.END, GridData.CENTER, false, true);
			add(contextLabel, contextGridData);
		}

		setTextLabel(label);
		setIconLabel(label);
	}

}
