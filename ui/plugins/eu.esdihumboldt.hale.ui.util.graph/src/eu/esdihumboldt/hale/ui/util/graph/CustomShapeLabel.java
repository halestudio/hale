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
package eu.esdihumboldt.hale.ui.util.graph;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

/**
 * Custom shaped label figure for use in graphs.
 * 
 * @author Simon Templer
 */
public class CustomShapeLabel extends CustomShapeFigure {

	/**
	 * Create a custom shaped label.
	 * 
	 * @param painter the painter drawing the figure shape
	 */
	public CustomShapeLabel(ShapePainter painter) {
		this(painter, null);
	}

	/**
	 * Create a custom shaped label.
	 * 
	 * @param painter the painter drawing the figure shape
	 * @param customFont a custom font to use for the text label, may be
	 *            <code>null</code>
	 */
	public CustomShapeLabel(ShapePainter painter, final Font customFont) {
		super(painter, customFont);

		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);

		Label label = new Label();
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		add(label, gridData);

		setTextLabel(label);
		setIconLabel(label);
	}

}
