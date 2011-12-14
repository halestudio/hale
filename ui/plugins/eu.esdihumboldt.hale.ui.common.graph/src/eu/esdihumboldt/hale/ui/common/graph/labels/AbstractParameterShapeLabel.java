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

package eu.esdihumboldt.hale.ui.common.graph.labels;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.zest.core.widgets.custom.CustomShapeFigure;

/**
 * The shape label for abstract parameters
 * @author Patrick
 */
public class AbstractParameterShapeLabel extends CustomShapeFigure {

	/**
	 * @param painter the painter drawing the figure shape
	 * @param text the text for the second label
	 */
	public AbstractParameterShapeLabel(ShapePainter painter, String text) {
		super(painter);
		
		setAntialias(SWT.ON);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 3;
		gridLayout.marginWidth = 3;
		setLayoutManager(gridLayout);

		Label namelabel = new Label();
		Label textlabel = new Label();
		textlabel.setText(text);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		add(namelabel, gridData);
		GridData gridData2 = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		add(textlabel, gridData2);

		setTextLabel(namelabel);
		setIconLabel(namelabel);
	}

}
