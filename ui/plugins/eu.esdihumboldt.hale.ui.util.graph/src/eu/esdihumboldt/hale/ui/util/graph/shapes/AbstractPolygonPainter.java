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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import eu.esdihumboldt.hale.ui.util.graph.CustomShapeFigure.ShapePainter;

/**
 * Abstract polygon shape painter.
 * 
 * @author Simon Templer
 */
public abstract class AbstractPolygonPainter implements ShapePainter {

	/**
	 * Get the figure's outline points
	 * 
	 * @param bounds the figure bounds
	 * @param lineWidth the line width of the border
	 * 
	 * @return the figure's outline
	 */
	protected abstract int[] getPoints(Rectangle bounds, int lineWidth);

	@Override
	public void fillShape(Graphics graphics, Rectangle bounds) {
		int[] points = getPoints(bounds, graphics.getLineWidth());
		graphics.fillPolygon(points);
	}

	@Override
	public void outlineShape(Graphics graphics, Rectangle bounds) {
		int[] points = getPoints(bounds, graphics.getLineWidth());
		graphics.drawPolygon(points);
	}

}
