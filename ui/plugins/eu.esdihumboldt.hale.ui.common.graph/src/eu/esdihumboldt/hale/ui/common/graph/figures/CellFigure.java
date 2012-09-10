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

import eu.esdihumboldt.hale.ui.util.graph.CustomShapeLabel;
import eu.esdihumboldt.hale.ui.util.graph.shapes.StretchedHexagon;

/**
 * Figure representing a cell.
 * 
 * @author Simon Templer
 */
public class CellFigure extends CustomShapeLabel {

	/**
	 * Default constructor
	 */
	public CellFigure() {
		super(new StretchedHexagon(10));
	}

}
