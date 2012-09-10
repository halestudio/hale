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
package eu.esdihumboldt.hale.ui.util.graph;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;

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
		super(painter);

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
