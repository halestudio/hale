/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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

import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure;

/**
 * The shape figure for abstract parameters
 * 
 * @author Patrick Lieb
 */
public class ParameterFigure extends CustomShapeFigure {

	/**
	 * @param painter
	 *            the painter drawing the figure shape
	 * @param occurrence
	 *            the occurrence of the figure
	 * @param description
	 *            the description of the figure
	 * @param showToolTip if the ToolTip should be shown
	 */
	public ParameterFigure(ShapePainter painter, String occurrence,
			String description, boolean showToolTip) {
		super(painter);
		
		final Display display = Display.getCurrent();

		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);
		
		Label textlabel = new Label(occurrence);
		IFigure occfig = new Label("Occurrence");
		textlabel.setToolTip(occfig);
		GridData textgrid = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		Font font = new Font(display, "Arial", 8, SWT.ITALIC);
		textlabel.setFont(font);
		add(textlabel, textgrid);

		Label namelabel = new Label();
		GridData namegrid = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		add(namelabel, namegrid);
		setTextLabel(namelabel);
		setIconLabel(namelabel);

		if(showToolTip){
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
					.getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION);
			Image image = fieldDecoration.getImage();

			Label descriptionlabel = new Label(image);
			IFigure descriptionfigure = new Label(description);
			descriptionlabel.setToolTip(descriptionfigure);
			GridData descriptiongrid = new GridData(GridData.FILL, GridData.FILL,
					true, true);
			add(descriptionlabel, descriptiongrid);
		}
	}

}
