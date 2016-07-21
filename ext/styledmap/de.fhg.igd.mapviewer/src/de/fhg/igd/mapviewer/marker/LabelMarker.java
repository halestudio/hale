/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */
package de.fhg.igd.mapviewer.marker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import de.fhg.igd.mapviewer.marker.area.Area;
import de.fhg.igd.mapviewer.marker.area.PolygonArea;

/**
 * A label marker
 * 
 * @param <T> the painter context type
 * @author Simon Templer
 */
public abstract class LabelMarker<T> extends SimpleMarker<T> {

	/**
	 * Get the paint color
	 * 
	 * @param context the context object
	 * @return the paint color for this object
	 */
	protected abstract Color getPaintColor(T context);

	// protected abstract Color getBorderColor(T context);

	/**
	 * Get the name of the given context object
	 * 
	 * @param context the context object
	 * 
	 * @return the name for this object
	 */
	protected String getName(T context) {
		return context.toString();
	}

	/**
	 * @see SimpleMarker#paintMarker(java.lang.Object)
	 */
	@Override
	protected Area paintMarker(T context) {
		String name = getName(context);
		Graphics2D dummy = getGraphicsDummy();

		int width = (int) dummy.getFontMetrics().getStringBounds(name, dummy).getWidth();

		Graphics2D g = beginPainting(width + 10, 30, width / 2 + 5, 0);
		try {
			g.setPaint(getPaintColor(context));
			Polygon triangle = new Polygon();
			triangle.addPoint(0, 0);
			triangle.addPoint(11, 11);
			triangle.addPoint(-11, 11);
			g.fill(triangle);
			g.fillRoundRect(-width / 2 - 5, 10, width + 10, 20, 10, 10);

			// g.setColor(borderColor);
			// g.drawRoundRect(-width/2 -5, 10, width+10, 20, 10, 10);

			// bounding polygon
			// (0,0), (11,11), (width/2 + 5, 10), (width/2 + 5, 30),
			// (-width/2 - 5, 30), (-width/2 - 5, 10), (-11, 11)
			Polygon bounds = new Polygon(new int[] { 0, 11, width / 2 + 5, width / 2 + 5,
					-width / 2 - 5, -width / 2 - 5, -11 }, new int[] { 0, 11, 10, 30, 30, 10, 11 },
					7);

			// draw text w/ shadow
			g.setPaint(Color.BLACK);
			g.drawString(name, -width / 2 - 1, 26 - 1); // shadow
			// g.drawString(name, -width/2-1, 26-1); //shadow
			g.setPaint(Color.WHITE);
			g.drawString(name, -width / 2, 26); // text

			return new PolygonArea(bounds);
		} finally {
			endPainting(g);
		}
	}

}
