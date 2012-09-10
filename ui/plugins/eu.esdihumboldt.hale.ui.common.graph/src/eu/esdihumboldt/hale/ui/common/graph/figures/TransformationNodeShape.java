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

import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure.ShapePainter;
import eu.esdihumboldt.hale.ui.util.graph.shapes.AbstractPolygonPainter;

/**
 * Transformation node shape
 * 
 * @author Simon Templer
 */
public class TransformationNodeShape extends AbstractPolygonPainter {

	private final int extWidth;

	private final int style;

	private final Insets insets;

	/**
	 * @param extWidth the extension width
	 * @param style the shape style, i.e. where the extension is added,
	 *            {@link SWT#LEFT}, {@link SWT#RIGHT} or {@link SWT#NONE}
	 */
	public TransformationNodeShape(int extWidth, int style) {
		super();
		this.extWidth = extWidth;
		this.style = style;

		if (style == 0) {
			insets = new Insets();
		}
		else if ((style & SWT.LEFT) == SWT.LEFT) {
			insets = new Insets(0, extWidth, 0, 0);
		}
		else {
			insets = new Insets(0, 0, 0, extWidth);
		}
	}

	/**
	 * @see ShapePainter#getInsets()
	 */
	@Override
	public Insets getInsets() {
		return insets;
	}

	/**
	 * @see AbstractPolygonPainter#getPoints(Rectangle, int)
	 */
	@Override
	protected int[] getPoints(Rectangle bounds, int lineWidth) {
		if (style == 0) {
			// just a rectangle
			int[] points = new int[8];

			points[0] = bounds.x;
			points[1] = bounds.y + lineWidth - 1;

			points[2] = bounds.right() - 1;
			points[3] = bounds.y + lineWidth - 1;

			points[4] = bounds.right() - 1;
			points[5] = bounds.bottom() - 1;

			points[6] = bounds.x;
			points[7] = bounds.bottom() - 1;

			return points;
		}

		int[] points = new int[10];

		if ((style & SWT.LEFT) == SWT.LEFT) {
			points[0] = bounds.x + extWidth;
			points[1] = bounds.y + bounds.height / 2;

			points[2] = bounds.x;
			points[3] = bounds.y + lineWidth - 1;

			points[4] = bounds.right() - 1;
			points[5] = bounds.y + lineWidth - 1;

			points[6] = bounds.right() - 1;
			points[7] = bounds.bottom() - 1;

			points[8] = bounds.x;
			points[9] = bounds.bottom() - 1;
		}
		else {
			points[0] = bounds.x;
			points[1] = bounds.y + lineWidth - 1;

			points[2] = bounds.right() - 1;
			points[3] = bounds.y + lineWidth - 1;

			points[4] = bounds.right() - extWidth - 1;
			points[5] = bounds.y + bounds.height / 2;

			points[6] = bounds.right() - 1;
			points[7] = bounds.bottom() - 1;

			points[8] = bounds.x;
			points[9] = bounds.bottom() - 1;
		}

		return points;
	}

}
