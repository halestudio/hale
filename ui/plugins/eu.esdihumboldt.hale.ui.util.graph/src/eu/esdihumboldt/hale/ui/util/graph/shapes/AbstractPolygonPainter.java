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
