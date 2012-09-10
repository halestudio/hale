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
import org.eclipse.swt.SWT;

/**
 * A fingerpost shape painter.
 * 
 * @author Simon Templer
 */
public class FingerPost extends AbstractPolygonPainter {

	private final int tipSize;

	private final boolean left;

	private Insets insets;

	/**
	 * Create a fingerpost shape painter.
	 * 
	 * @param tipSize the finger tip size
	 * @param style the shape style, either pointing {@link SWT#LEFT} or
	 *            {@link SWT#RIGHT}
	 */
	public FingerPost(int tipSize, int style) {
		super();
		this.tipSize = tipSize;

		this.left = (style & SWT.LEFT) == SWT.LEFT;

		if (left) {
			insets = new Insets(0, tipSize, 0, 0);
		}
		else {
			insets = new Insets(0, 0, 0, tipSize);
		}
	}

	@Override
	public Insets getInsets() {
		return insets;
	}

	@Override
	protected int[] getPoints(Rectangle bounds, int lineWidth) {
		int[] points = new int[10];

		if (left) {
			points[0] = bounds.x;
			points[1] = bounds.y + bounds.height / 2;

			points[2] = bounds.x + tipSize;
			points[3] = bounds.y + lineWidth - 1;

			points[4] = bounds.right() - 1;
			points[5] = bounds.y + lineWidth - 1;

			points[6] = bounds.right() - 1;
			points[7] = bounds.bottom() - 1;

			points[8] = bounds.x + tipSize;
			points[9] = bounds.bottom() - 1;
		}
		else {
			points[0] = bounds.x;
			points[1] = bounds.y + lineWidth - 1;

			points[2] = bounds.right() - tipSize - 1;
			points[3] = bounds.y + lineWidth - 1;

			points[4] = bounds.right() - 1;
			points[5] = bounds.y + bounds.height / 2;

			points[6] = bounds.right() - tipSize - 1;
			points[7] = bounds.bottom() - 1;

			points[8] = bounds.x;
			points[9] = bounds.bottom() - 1;
		}

		return points;
	}

}
