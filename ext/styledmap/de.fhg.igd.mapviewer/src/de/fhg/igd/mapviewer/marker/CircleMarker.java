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
 * A circle shaped marker
 * 
 * @param <T> the painter context type
 * @author Simon Templer
 */
public abstract class CircleMarker<T> extends SimpleMarker<T> {

	private final int size;

	/**
	 * Creates a circle marker with the given size
	 * 
	 * @param size the circle size
	 */
	public CircleMarker(final int size) {
		this.size = size;
	}

	/**
	 * Get the paint color
	 * 
	 * @param context the painting context
	 * @return the paint color
	 */
	protected abstract Color getPaintColor(T context);

	/**
	 * Get the border color
	 * 
	 * @param context the painting context
	 * @return the border color
	 */
	protected abstract Color getBorderColor(T context);

	/**
	 * Get the marker color
	 * 
	 * @param context the painting context
	 * @return the marker color
	 */
	protected abstract Color getMarkerColor(T context);

	/**
	 * Determine if a marker shall be shown
	 * 
	 * @param context the painting context
	 * @return if a marker shall be shown
	 */
	protected abstract boolean showMarker(T context);

	/**
	 * @see SimpleMarker#paintMarker(Object)
	 */
	@Override
	protected Area paintMarker(T context) {
		int maxSize = Math.max(2 * 11, size + 2);

		Graphics2D g = beginPainting(maxSize, maxSize, maxSize / 2, maxSize / 2);
		try {
			g.setPaint(getPaintColor(context));
			g.fillOval(-size / 2, -size / 2, size, size);

			g.setColor(getBorderColor(context));
			g.drawOval(-size / 2 - 1, -size / 2 - 1, size + 1, size + 1);

			if (showMarker(context)) {
				g.setPaint(getMarkerColor(context));
				Polygon triangle = new Polygon();
				triangle.addPoint(0, 0);
				triangle.addPoint(-11, -11);
				triangle.addPoint(11, -11);
				g.fill(triangle);

				return new PolygonArea(
						new Polygon(new int[] { -11, 11 + 1, size / 2 + 1, -size / 2 - 1 },
								new int[] { -11, -11, size / 2 + 1, size / 2 + 1 }, 4));
			}
			else
				return new PolygonArea(new Polygon(
						new int[] { -size / 2 - 1, size / 2 + 1, size / 2 + 1, -size / 2 - 1 },
						new int[] { -size / 2 - 1, -size / 2 - 1, size / 2 + 1, size / 2 + 1 }, 4));
		} finally {
			endPainting(g);
		}
	}

}
