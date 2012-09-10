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
package eu.esdihumboldt.hale.ui.util.graph.shapes;

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Stretched hexagon example shape.
 * 
 * @author Simon Templer
 */
public class StretchedHexagon extends AbstractPolygonPainter {

	private final int inset;

	private final Insets insets;

	/**
	 * Creates a stretched hexagon shape.
	 * 
	 * @param inset the horizontal inset in pixels
	 */
	public StretchedHexagon(int inset) {
		super();
		this.inset = inset;

		insets = new Insets(0, inset, 0, inset);
	}

	/**
	 * Get the figure's outline points
	 * 
	 * @return the figure's outline
	 */
	@Override
	protected int[] getPoints(Rectangle bounds, int lineWidth) {
		int[] points = new int[12];

		points[0] = bounds.x;
		points[1] = bounds.y + bounds.height / 2;

		points[2] = bounds.x + inset;
		points[3] = bounds.y + lineWidth - 1;

		points[4] = bounds.right() - inset - 1;
		points[5] = bounds.y + lineWidth - 1;

		points[6] = bounds.right() - 1;
		points[7] = bounds.y + bounds.height / 2;

		points[8] = bounds.right() - inset - 1;
		points[9] = bounds.bottom() - 1;

		points[10] = bounds.x + inset;
		points[11] = bounds.bottom() - 1;

		return points;
	}

	@Override
	public Insets getInsets() {
		return insets;
	}

}
