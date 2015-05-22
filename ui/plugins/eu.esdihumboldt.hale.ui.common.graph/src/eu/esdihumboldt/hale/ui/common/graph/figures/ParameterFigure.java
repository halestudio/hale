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
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;

/**
 * The shape figure for the defined parameters in {@link FunctionDefinition}(s)
 * 
 * @author Patrick Lieb
 */
public class ParameterFigure extends CustomShapeFigure {

	/**
	 * @param painter the painter drawing the figure shape
	 * @param occurrence the occurrence of the figure
	 * @param description the description of the figure
	 * @param showToolTip if the ToolTip should be shown
	 */
	public ParameterFigure(ShapePainter painter, String occurrence, String description,
			boolean showToolTip) {
		super(painter);

		final Display display = Display.getCurrent();

		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);

		Label namelabel = new Label();
		GridData namegrid = new GridData(GridData.FILL, GridData.FILL, true, true);
		add(namelabel, namegrid);
		setTextLabel(namelabel);
		setIconLabel(namelabel);

		Label textlabel = new Label(occurrence);
		GridData textgrid = new GridData(GridData.FILL, GridData.FILL, true, true);
		Font font = new Font(display, "Arial", 8, SWT.ITALIC);
		textlabel.setFont(font);
		add(textlabel, textgrid);

		if (showToolTip) {
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
			Image image = fieldDecoration.getImage();

			Label descriptionlabel = new Label(image);
			IFigure descriptionfigure = new Label(description);
			descriptionlabel.setToolTip(descriptionfigure);
			GridData descriptiongrid = new GridData(GridData.FILL, GridData.FILL, true, true);
			add(descriptionlabel, descriptiongrid);
		}
	}

}
